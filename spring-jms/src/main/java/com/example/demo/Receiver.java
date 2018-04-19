package com.example.demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @JmsListener(destination = "my-queue")
    public void receiveMessage(String message) {
        System.out.println(message);
    }
}
