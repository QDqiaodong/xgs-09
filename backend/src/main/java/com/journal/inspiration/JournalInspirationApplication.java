package com.journal.inspiration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.journal.inspiration.mapper")
@EnableCaching
public class JournalInspirationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JournalInspirationApplication.class, args);
    }
}
