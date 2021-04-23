package study.query.dsl.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.query.dsl.querydsl.entity.Hello;
import study.query.dsl.querydsl.entity.QHello;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class QueryDslApplicationTests {
    @PersistenceContext
    EntityManager em;
    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);
        JPAQueryFactory query=new JPAQueryFactory(em);

        Hello findHello = query.selectFrom(QHello.hello).fetchOne();

        assertThat(findHello.getId()).isEqualTo(hello.getId());
    }

}
