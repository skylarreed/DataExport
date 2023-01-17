package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sr
 * @ClassName MerchantProcessor
 * @Description This class is used to process the transactions return the transaction if it matches the id provided.
 */
@Component
@StepScope
public class MerchantProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['merchantId']}")
    private long merchantId;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        if (transaction.getMerchantId() == merchantId) {
            return transaction;
        }

        return null;
    }
}
