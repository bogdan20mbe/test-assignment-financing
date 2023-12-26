package lu.crx.test.financing.repositories;

import lu.crx.test.financing.entities.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Slice<Invoice> findAllByFinancedIsFalseAndEligibleToBeFinancedIsTrue(Pageable page);
}
