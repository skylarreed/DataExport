package com.sr.dataexport.readers;

import com.sr.dataexport.exceptions.FileNotValidException;
import com.sr.dataexport.models.User;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class UserReader {
    @Bean
    public SynchronizedItemStreamReader<User> synchronizedReader(@Value("#{jobParameters[filePath]}") String filePath) {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(filePath));
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper((line, lineNumber) -> {
            String[] fields = line.split(",");
            User user = new User();
            try{
                if(fields[0] != null && !fields[0].isEmpty()){
                    user.setUserId(Long.parseLong(fields[0]));
                }
            }catch (NumberFormatException e){
                throw new FileNotValidException("File is not valid");
            }
            return user;
        });
        SynchronizedItemStreamReader<User> synchronizedItemStreamReader = new SynchronizedItemStreamReader<>();
        synchronizedItemStreamReader.setDelegate(itemReader);
        return synchronizedItemStreamReader;
    }
}
