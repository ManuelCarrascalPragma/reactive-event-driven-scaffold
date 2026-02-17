package co.com.bancolombia.r2dbc.transaction;

import co.com.bancolombia.model.transaction.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("transactions")
public class TransactionEntity implements Persistable<String> {
    @Id
    private String id;
    
    @Column("account_id")
    private String accountId;
    
    private Double amount;
    private String currency;
    private String status;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    @Transient
    @Builder.Default
    private boolean isNew = true;

    public static TransactionEntity fromDomain(Transaction transaction) {
        return TransactionEntity.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isNew(true)
                .build();
    }

    public Transaction toDomain() {
        return Transaction.builder()
                .id(id)
                .accountId(accountId)
                .amount(amount)
                .currency(currency)
                .status(status)
                .build();
    }
}
