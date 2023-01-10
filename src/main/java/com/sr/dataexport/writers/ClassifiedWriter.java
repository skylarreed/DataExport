package com.sr.dataexport.writers;

import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.writers.classifiers.UserClassifier;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ClassifiedWriter {

    private final UserClassifier userClassifier;

    public ClassifiedWriter(UserClassifier userClassifier) {
        this.userClassifier = userClassifier;
    }

    @Bean
    public ClassifierCompositeItemWriter<Transaction> classifierWriter() {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(userClassifier);
        return classifier;
    }
}
