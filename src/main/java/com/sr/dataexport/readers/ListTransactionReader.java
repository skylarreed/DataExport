package com.sr.dataexport.readers;


import com.sr.dataexport.models.Transaction;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListTransactionReader implements ItemReader<Transaction> {

    public static List<Transaction> transactions;

    @Override
    public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        synchronized (this) {
            if (transactions != null && !transactions.isEmpty()) {
                return transactions.remove(0);
            }
        }
        return null;
    }
}
