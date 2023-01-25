package com.sr.dataexport.classifiers;

import com.sr.dataexport.models.Transaction;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sr
 * @ClassName UserClassifier
 * @Description This class implements the Classifier interface and is used to classify the transactions based on
 * the merchant id. It saves each merchant's transactions in a separate file.
 */
@Component
@StepScope
@Slf4j
public class MerchantClassifier implements Classifier<Transaction, ItemWriter<? super Transaction>> {

    private final Map<String, ItemWriter<? super Transaction>> writerMap;

    @Value("#{jobParameters['destination']}")
    private String destination;

    public MerchantClassifier() {
        this.writerMap = new HashMap<>();
    }

    /**
     * @param transaction
     * @return ItemWriter<? super Transaction>
     * @Description This method classifies the transactions based on the merchant id. It then stores the transactions
     * in a map with the file name as the key. If the file name is already present in the map, it returns the
     * corresponding writer. If the file name is not present in the map, it creates a new writer and adds it to the map.
     */
    @Override
    public ItemWriter<? super Transaction> classify(Transaction transaction) {
        String fileName = destination + "/" + "merchant-" + transaction.getMerchantId() + "-transactions" + ".xml";
        synchronized (this) {
            if (writerMap.containsKey(fileName)) {
                return writerMap.get(fileName);
            }

            XStreamMarshaller marshaller = new XStreamMarshaller();
            marshaller.setAliases(Collections.singletonMap("transaction", Transaction.class));
            marshaller.setAnnotatedClasses(Transaction.class);

            StaxEventItemWriter<Transaction> writer = new StaxEventItemWriterBuilder<Transaction>()
                    .name("MerchantTransactionWriter")
                    .rootTagName("MerchantTransactions")
                    .marshaller(marshaller)
                    .resource(new FileSystemResource(fileName))
                    .transactional(false)
                    .build();

            SynchronizedItemStreamWriter<Transaction> synchronizedWriter = new SynchronizedItemStreamWriterBuilder<Transaction>()
                    .delegate(writer)
                    .build();


            synchronizedWriter.open(new ExecutionContext());

            writerMap.put(fileName, synchronizedWriter);

            return synchronizedWriter;
        }
    }

    /**
     * @Description This method closes all the writers in the map. Then it clears the map.
     */

    public void close() {
        for(String s : writerMap.keySet()){
            SynchronizedItemStreamWriter<Transaction> writer = (SynchronizedItemStreamWriter<Transaction>) writerMap.get(s);
            writer.close();
        }
        writerMap.clear();
    }

}
