package lu.crx.test.financing;

import lu.crx.test.financing.repositories.FinancingResultRepository;
import lu.crx.test.financing.repositories.InvoiceRepository;
import lu.crx.test.financing.services.FinancingService;
import lu.crx.test.financing.services.SeedingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
public class TestAssignmentFinancingApplicationTest {

    @Autowired
    private SeedingService seedingService;

    @Autowired
    private FinancingService financingService;

    @Autowired
    private FinancingResultRepository financingResultRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    void init() {

    }

    @Test
    void runApp_financingIsExecuted_financingResultsAreSaved() {
    }

    //TODO implement this integration test with different scenarios
    //TODO add unit tests
}
