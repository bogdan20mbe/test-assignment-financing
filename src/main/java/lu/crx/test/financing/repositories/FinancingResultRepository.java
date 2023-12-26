package lu.crx.test.financing.repositories;

import lu.crx.test.financing.entities.FinancingResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancingResultRepository extends JpaRepository<FinancingResults, Long> {
}
