package lu.crx.test.financing.services;

import lombok.extern.slf4j.Slf4j;
import lu.crx.test.financing.entities.FinancingResults;
import lu.crx.test.financing.entities.Invoice;
import lu.crx.test.financing.repositories.FinancingResultRepository;
import lu.crx.test.financing.repositories.InvoiceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service
public class FinancingService {

    private final InvoiceRepository invoiceRepository;
    private final EntityManager entityManager;
    private final FinancingResultRepository financingResultRepository;
    //TODO config the size according to expected number of entries
    private static final int BATCH_SIZE = 5;

    public FinancingService(InvoiceRepository invoiceRepository, EntityManager entityManager, FinancingResultRepository financingResultRepository) {
        this.invoiceRepository = invoiceRepository;
        this.entityManager = entityManager;
        this.financingResultRepository = financingResultRepository;
    }

    @Transactional
    public void finance() {
        log.info("Financing started");

        Slice<Invoice> slice = invoiceRepository.findAllByFinancedIsFalseAndEligibleToBeFinancedIsTrue(PageRequest.of(0, BATCH_SIZE));
        financeInvoicesBatch(slice.getContent());

        while(slice.hasNext()) {
            // always get the first page because we update in the same time
            slice = invoiceRepository.findAllByFinancedIsFalseAndEligibleToBeFinancedIsTrue(PageRequest.of(0, BATCH_SIZE));
            financeInvoicesBatch(slice.getContent());
        }

        log.info("Total number of financing results={}", financingResultRepository.findAll().size());
        log.info("Financing completed");
    }

    private void financeInvoicesBatch(List<Invoice> invoices) {
        invoices.forEach(invoice -> {
            var possibleFinancingResults = getPossibleFinancingResults(invoice);
            saveFinancialResults(invoice, possibleFinancingResults);
        });
    }

    private TreeMap<Integer, FinancingResults> getPossibleFinancingResults(Invoice invoice) {
        var creditor = invoice.getCreditor();
        var purchaserFinancingSettings = creditor.getPurchaserFinancingSettings();
        var possibleFinancingResults = new TreeMap<Integer, FinancingResults>();

        purchaserFinancingSettings.forEach(financingSetting -> {
            var purchaser = financingSetting.getPurchaser();

            var financingTerm = LocalDate.now().until(invoice.getMaturityDate(), ChronoUnit.DAYS);
            if (financingTerm >= purchaser.getMinimumFinancingTermInDays()) {
                var financingRate = (int) (financingSetting.getAnnualRateInBps() * financingTerm / 360);
                if (financingRate <= creditor.getMaxFinancingRateInBps()) {
                    var earlyPaymentAmount = invoice.getValueInCents() - financingRate;
                    possibleFinancingResults.put(financingRate,
                            FinancingResults.builder()
                                    .purchaser(purchaser)
                                    .financingTerm(financingTerm)
                                    .financingRate(financingRate)
                                    .financingDate(LocalDate.now())
                                    .earlyPaymentAmount(earlyPaymentAmount)
                                    .build());
                }
            }
        });
        return possibleFinancingResults;
    }

    private void saveFinancialResults(Invoice invoice, TreeMap<Integer, FinancingResults> possibleFinancingResults) {
        if (!possibleFinancingResults.isEmpty()) {
            invoice.setFinanced(true);
            var financingResult = possibleFinancingResults.firstEntry().getValue();
            invoice.setFinancingResults(financingResult);

            log.info("Invoice={}, financed with results: term={}, rate={}, date={}, amount={}",
                    invoice.getId(), financingResult.getFinancingTerm(), financingResult.getFinancingRate(),
                    financingResult.getFinancingDate(), financingResult.getEarlyPaymentAmount());
        } else {
            invoice.setEligibleToBeFinanced(false);
            log.warn("Invoice={} can't be financed!", invoice.getId());
        }
        entityManager.persist(invoice);    
    }

}
