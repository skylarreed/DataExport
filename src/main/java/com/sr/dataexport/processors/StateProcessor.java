package com.sr.dataexport.processors;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName StateProcessor
 * @Description This class is used to process the transaction based on the state.
 */
@Component
@StepScope
public class StateProcessor implements ItemProcessor<Transaction, Transaction> {

    @Value("#{jobParameters['state']}")
    private String state;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        try{
            if(transaction.getMerchantState().equals(state)){
                return transaction;
            }
        } catch (NullPointerException e){
            return null;
        }
        return null;
    }
}
