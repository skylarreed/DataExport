package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * @ClassName TransactionTypeProcessor
 * @Description This class is used to process the transaction based on the transaction type.
 */
@Component
@StepScope
public class TransactionTypeProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['transactionType']}")
    private String transactionType;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        if(transaction.getType().equals(transactionType)) {
            return transaction;
        }
        return null;
    }

}

