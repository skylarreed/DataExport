package com.sr.dataexport.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * @ClassName MainChunkListener
 * @Description This class is used to listen to the chunk events.
 */
@Slf4j(topic = "MainChunkListener")
public class MainChunkListener implements ChunkListener {
    private int count = 0;

    @Override
    public void beforeChunk(ChunkContext chunkContext) {
    }

    /**
     * This method is used to log the chunk count, and log the current read and write in intervals.
     * @param chunkContext
     */
    @Override
    public void afterChunk(ChunkContext chunkContext) {

        if(count % 50 == 0 && count != 0) {
            log.info("Writing: " + chunkContext.getStepContext().getStepName() + " with " + chunkContext.getStepContext().getStepExecution().getWriteCount() +
                    " records" + " for " + chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName());
        }
        if(count % 100 == 0 && count != 0) {
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
