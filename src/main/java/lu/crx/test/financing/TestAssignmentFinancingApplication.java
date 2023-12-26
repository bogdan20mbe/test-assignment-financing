package lu.crx.test.financing;

import lu.crx.test.financing.services.FinancingService;
import lu.crx.test.financing.services.SeedingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestAssignmentFinancingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestAssignmentFinancingApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(
            SeedingService seedingService,
            FinancingService financingService) {

        return args -> {
            // seeding master data - creditors, debtors and purchasers
            seedingService.seedMasterData();

            // seeding the invoices
            seedingService.seedInvoices();

            // running the financing
            financingService.finance();
        };
    }
    //********************************
    //          NOTES
    //Steps for further work to be done
    //1. Analyze if the main query can be split in a few ones to reduce the number of generated sqls
    //2. Analyze if the processing of a batch is faster using parallel stream or recursive task
    //3. Write tests
    //********************************
}
