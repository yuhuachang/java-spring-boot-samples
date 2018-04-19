package com.example.demo;

import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1000);
        executor.setMaxPoolSize(10000);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }
    
    @GetMapping("send")
    public String sendMessage(@RequestParam String payload) throws JMSException {

        TaskExecutor taskExecutor = threadPoolTaskExecutor();
        
        for (int i = 0; i < 1000; i++) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    jmsTemplate.setDefaultDestinationName(JmsConfig.QUEUE_NAME);
                    jmsTemplate.convertAndSend(payload);
                    System.out.println("sent msg");
                }
            });
        }
        return "done";
    }

}
