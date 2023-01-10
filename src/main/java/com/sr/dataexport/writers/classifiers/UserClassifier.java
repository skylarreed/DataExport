package com.sr.dataexport.writers.classifiers;

import com.sr.dataexport.models.Transaction;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
@Slf4j
public class UserClassifier implements Classifier<Transaction, ItemWriter<? super Transaction>> {

    private final Map<String, ItemWriter<? super Transaction>> writerMap;

    @Value("#{jobParameters['destination']}")
    private String destination;

    public UserClassifier() {
        this.writerMap = new HashMap<>();
    }
    @Override
    public ItemWriter<? super Transaction> classify(Transaction transaction) {
        String fileName = destination + "/" + "user-" + transaction.getUserId() + "-transactions" + ".xml";
        synchronized (this) {
            if (writerMap.containsKey(fileName)) {
                return writerMap.get(fileName);
            }
            File tempFile = new File(fileName);
            try{
                if(!tempFile.exists()){
                    tempFile.createNewFile();
                }
            } catch (IOException e) {
                log.info("File already exists");
            }

            XStreamMarshaller marshaller = new XStreamMarshaller();
            marshaller.setAliases(Collections.singletonMap("transaction", Transaction.class));
            StaxEventItemWriter<Transaction> writer = new StaxEventItemWriterBuilder<Transaction>()
                    .name("userTransactionWriter")
                    .rootTagName("transactions")
                    .marshaller(marshaller)
                    .resource(new FileSystemResource(tempFile.getAbsolutePath()))
                    .overwriteOutput(true)
                    .build();

            SynchronizedItemStreamWriter<Transaction> synchronizedWriter = new SynchronizedItemStreamWriterBuilder<Transaction>()
                    .delegate(writer)
                    .build();

            synchronizedWriter.open(new ExecutionContext());
            writerMap.put(fileName, synchronizedWriter);
            return synchronizedWriter;
        }
    }

    public void close() {
        writerMap.clear();
    }
}
