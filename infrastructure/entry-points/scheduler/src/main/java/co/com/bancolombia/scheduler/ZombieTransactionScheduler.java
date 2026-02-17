package co.com.bancolombia.scheduler;

import co.com.bancolombia.usecase.transaction.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log
@Component
@RequiredArgsConstructor
public class ZombieTransactionScheduler {
    
    private final TransactionUseCase transactionUseCase;
    private static final int TIMEOUT_MINUTES = 10;
    
    @Scheduled(fixedRate = 300000)
    public void cleanupZombieTransactions() {
        transactionUseCase.processZombieTransactions(TIMEOUT_MINUTES)
                .subscribe(
                        count -> {
                            if (count > 0) {
                                log.info("Processed " + count + " zombie transactions (PENDING > " + TIMEOUT_MINUTES + " minutes)");
                            }
                        },
                        error -> log.severe("Error processing zombie transactions: " + error.getMessage())
                );
    }
}
