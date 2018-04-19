package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyService {

    @Autowired
    private MyMapper mapper;
    
    @Transactional
    public void doJob(String name, String value) {
        mapper.insert(name, value);
        mapper.insert("John", "12345");
    }
}
