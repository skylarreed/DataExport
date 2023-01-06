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
public class TransactionReader {
    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Transaction> synchronizedReader(@Value("#{jobParameters[filePath]}") String filePath) {
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
                    if (fields[1] != null && !fields[1].isEmpty()) {
                        transaction.setCardId(Long.parseLong(fields[1]));
                    }
                    if (fields[2] != null && !fields[2].isEmpty()) {
                        transaction.setYear(Integer.parseInt(fields[2]));
                    }
                    if (fields[3] != null && !fields[3].isEmpty()) {
                        transaction.setMonth(Integer.parseInt(fields[3]));
                    }
                    if (fields[4] != null && !fields[4].isEmpty()) {
                        transaction.setDay(Integer.parseInt(fields[4]));
                    }
                    if (fields[5] != null && !fields[5].isEmpty()) {
                        transaction.setTime(fields[5]);
                    }
                    if (fields[6] != null && !fields[6].isEmpty()) {
                        transaction.setAmount(Double.parseDouble(fields[6].replace("$", "")));
                    }
                    if (fields[7] != null && !fields[7].isEmpty()) {
                        transaction.setType(fields[7]);
                    }
                    if (fields[8] != null && !fields[8].isEmpty()) {
                        transaction.setMerchantId(Long.parseLong(fields[8]));
                    }
                    if (fields[9] != null && !fields[9].isEmpty()) {
                        transaction.setMerchantCity(fields[9]);
                    }
                    if (fields[10] != null && !fields[10].isEmpty()) {
                        transaction.setMerchantState(fields[10]);
                    }
                    if (fields[11] != null && !fields[11].isEmpty()) {
                        transaction.setZip(fields[11]);
                    }
                    if (fields[12] != null && !fields[12].isEmpty()) {
                        transaction.setMcc(Integer.parseInt(fields[12]));
                    }
                    if (fields[13] != null && !fields[13].isEmpty()) {
                        transaction.setErrors(fields[13]);
                    }
                    if (fields[14] != null && !fields[14].isEmpty()) {
                        if (fields[14].equals("yes")) {
                            transaction.setFraud(true);
                        } else {
                            transaction.setFraud(false);
                        }
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

