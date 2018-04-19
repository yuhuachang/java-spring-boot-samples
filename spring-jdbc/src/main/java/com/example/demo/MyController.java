package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/")
    public List<String> getTables() {
        return jdbcTemplate.query("select table_name from information_schema.tables", (rs, rowNum) -> {
            return rs.getString("table_name");
        });
    }
}
