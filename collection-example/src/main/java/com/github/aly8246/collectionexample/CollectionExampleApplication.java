package com.github.aly8246.collectionexample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.github.aly8246.collectionutil",
        "com.github.aly8246.collectionexample"
})
@MapperScan({"com.github.aly8246.collectionexample.dao"})
public class CollectionExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectionExampleApplication.class, args);
    }

}
