package com.github.aly8246.collectionutiltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.github.aly8246.collectionutil",
        "com.github.aly8246.collectionutiltest"
})
@MapperScan({"com.github.aly8246.collectionutiltest.dao"})
public class CollectionUtilTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectionUtilTestApplication.class, args);
    }

}
