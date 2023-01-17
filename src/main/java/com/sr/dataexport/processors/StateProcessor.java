package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class StateProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['state']}")
    private String state;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        if(transaction.getMerchantState().equals(state)) {
            return transaction;
        }
        return null;
    }
}
