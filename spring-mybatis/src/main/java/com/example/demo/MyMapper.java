package com.example.demo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyMapper {

    int create();

    List<Map<String, Object>> select();

    int insert(@Param("name") String name, @Param("value") String value);
}
