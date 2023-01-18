package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName YearProcessor
 * @Description This class is used to process the transaction based on the year.
 */
@Component
@StepScope
public class YearProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['year']}")
    private String year;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        int iYear = Integer.parseInt(year);
        if(transaction.getYear() == iYear) {
            return transaction;
        }
        return null;
    }

}

