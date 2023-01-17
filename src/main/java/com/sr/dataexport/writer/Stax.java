package com.sr.dataexport.writer;

import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class Stax {
    @Bean(name = "staxWriter")
    @StepScope
    public SynchronizedItemStreamWriter<Transaction> staxEventItemWriter(@Value("#{jobParameters['destination']}") String destination) {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(Collections.singletonMap("transaction", Transaction.class));

        StaxEventItemWriter<Transaction> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setResource(new FileSystemResource(destination));
        staxEventItemWriter.setRootTagName("transactions");
        staxEventItemWriter.setMarshaller(marshaller);
        return new SynchronizedItemStreamWriterBuilder<Transaction>().delegate(staxEventItemWriter).build();
    }
}
