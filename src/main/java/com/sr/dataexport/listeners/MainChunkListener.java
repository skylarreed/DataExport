package com.sr.dataexport.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j(topic = "MainChunkListener")
public class MainChunkListener implements ChunkListener {
    private int count = 0;

    @Override
    public void beforeChunk(ChunkContext chunkContext) {
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {

        if(count % 200 == 0 && count != 0) {
            log.info("Writing: " + chunkContext.getStepContext().getStepName() + " with " + chunkContext.getStepContext().getStepExecution().getWriteCount() +
                    " records" + " for " + chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName());
        }
        if(count % 400 == 0 && count != 0) {
            log.info("Reading: " + chunkContext.getStepContext().getStepName() + " with " + chunkContext.getStepContext().getStepExecution().getReadCount() +
                    " records" + " for " + chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName());
        }
        count++;

        chunkContext.getStepContext().getJobParameters().get("userId");
    }

    @Override
    public void afterChunkError(ChunkContext chunkContext) {
        log.error("Error in chunk: " + chunkContext.getStepContext().getStepName());
    }
}
