package com.example.demo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private MyMapper mapper;
    
    @Autowired
    private MyService service;

    @GetMapping("/create")
    public int create() {
        return mapper.create();
    }

    @Transactional
    @GetMapping("/select")
    public List<Map<String, Object>> select() {
        return mapper.select();
    }
    
    @Transactional
    @GetMapping("/insert")
    public int insert(@RequestParam("name") String name, @RequestParam("value") String value) {
        return mapper.insert(name, value);
    }
    
    @GetMapping("/doJob")
    public void doJob(@RequestParam("name") String name, @RequestParam("value") String value) {
        service.doJob(name, value);
    }

}
