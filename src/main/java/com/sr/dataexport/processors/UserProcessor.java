package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sr
 * @ClassName UserProcessor
 * @Description This class is used to process the transactions return the transaction if it matches the id provided.
 */
@Component
@StepScope
public class UserProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['userId']}")
    private String userId;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        if(transaction.getUserId() == Long.parseLong(userId)) {
            return transaction;
        }
        return null;
    }
}

