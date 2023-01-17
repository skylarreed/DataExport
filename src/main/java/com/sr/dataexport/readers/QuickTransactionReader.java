package com.sr.dataexport.readers;

import com.sr.dataexport.exceptions.FileNotValidException;
import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class QuickTransactionReader {
    @Bean(name = "QuickTransactionsReader")
    @StepScope
    public SynchronizedItemStreamReader<Transaction> transactionReader(@Value("#{jobParameters['filePath']}") String filePath) {
        FlatFileItemReader<Transaction> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(filePath));
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper((line, lineNumber) -> {
            String[] fields = line.split(",");
            Transaction transaction = new Transaction();
            try{
                if (fields[0] != null && !fields[0].isEmpty()) {
                    transaction.setUserId(Long.parseLong(fields[0]));
                }
                if (fields[8] != null && !fields[8].isEmpty()) {
                    transaction.setMerchantId(Long.parseLong(fields[8]));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new FileNotValidException("File is not valid");
            }
            return transaction;
        });

        SynchronizedItemStreamReader<Transaction> synchronizedReader = new SynchronizedItemStreamReader<>();
        synchronizedReader.setDelegate(itemReader);
        return synchronizedReader;
    }
}
