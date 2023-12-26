package lu.crx.test.financing.entities;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * An invoice issued by the {@link Creditor} to the {@link Debtor} for shipped goods.
 */
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * Creditor is the entity that issued the invoice.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creditor_id")
    private Creditor creditor;

    /**
     * Debtor is the entity obliged to pay according to the invoice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debtor_id")
    private Debtor debtor;

    /**
     * Maturity date is the date on which the {@link #debtor} is to pay for the invoice.
     * In case the invoice was financed, the money will be paid in full on this date to the purchaser of the invoice.
     */
    @Basic(optional = false)
    private LocalDate maturityDate;

    /**
     * The value is the amount to be paid for the shipment by the Debtor.
     */
    @Basic(optional = false)
    private long valueInCents;

    /**
     * This flag indicates if an invoice has financial results.
     */
    @Basic(optional = false)
    private boolean financed;

    /**
     * This flag is used for slicing the non-financed invoices.
     */
    @Basic(optional = false)
    private boolean eligibleToBeFinanced;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "financing_results_id")
    private FinancingResults financingResults;

}
