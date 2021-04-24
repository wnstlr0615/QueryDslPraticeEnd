package study.query.dsl.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QueryDslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryDslApplication.class, args);
    }
    @Bean
    JPAQueryFactory getJPAQueryFactory(EntityManager em){
        return new JPAQueryFactory(em);
    }
}
