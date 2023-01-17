package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class YearProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['year']}")
    private int year;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        if(transaction.getYear() == year) {
            return transaction;
        }
        return null;
    }

}

