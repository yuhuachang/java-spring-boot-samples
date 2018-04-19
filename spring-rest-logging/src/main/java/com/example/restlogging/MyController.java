package com.example.restlogging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final Log logger = LogFactory.getLog(getClass());

    public static class Person {

        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + "]";
        }

    }

    @GetMapping("/")
    public Person getPerson(@RequestParam String name, @RequestParam(defaultValue = "20", required = false) int age) {
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        logger.info("New person created: " + person);
        return person;
    }

    @PostMapping("/")
    public Person gettingOlder(@RequestBody Person person) {
        int age = person.getAge();
        person.setAge(age + 1);
        logger.info(person.getName() + " is getting older. Now " + person.getAge() + " years old.");
        return person;
    }
}
