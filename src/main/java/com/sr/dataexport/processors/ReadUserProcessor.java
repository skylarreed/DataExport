package com.sr.dataexport.processors;

import com.sr.dataexport.models.User;
import com.sr.dataexport.utils.UserTracker;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ReadUserProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User user) throws Exception {
        synchronized (this){
            if(UserTracker.users.containsKey(user.getUserId())){
                return null;
            } else {
                UserTracker.users.put(user.getUserId(), "Running");
                return user;
            }
        }
    }
}
