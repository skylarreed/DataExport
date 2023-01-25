package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.MerchantClassifier;
import com.sr.dataexport.processors.MerchantProcessor;
import com.sr.dataexport.readers.TransactionReader;
import com.sr.dataexport.writer.Stax;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@EnableAutoConfiguration
@SpringBatchTest
@SpringJUnitConfig(classes = {MerchantExportConfig.class, BatchTaskExecutor.class,
        TransactionReader.class, MerchantClassifier.class, Stax.class, MerchantProcessor.class}, name = "merchantExportJob")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MerchantExportConfigTest {


    @Autowired
    @Qualifier("merchantExportJob")
    private Job exportAllMerchantsJob;


    @Test
    public void smokeTest() throws Exception {

    }
}