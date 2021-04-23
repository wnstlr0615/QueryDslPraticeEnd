package study.query.dsl.querydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberTest {
    @Autowired
    EntityManager em;
    @Test
    @Description("페치조인을 사용하지 않아 N+1 문제 발생")
    public void testEntity() throws Exception{
        //given
        Team teamA = createTeam("teamA");
        Team teamB = createTeam("teamB");

        Member member1 = createMember("member1", 10, teamA);
        Member member2 = createMember("member2", 10, teamA);
        Member member3 = createMember("member3", 10, teamA);
        Member member4 = createMember("member4", 10, teamA);

        em.flush();
        em.clear();

        //when
        List<Member> findMembers = em.createQuery("" +
                "select m from Member m"
                , Member.class).getResultList();
        PersistenceUtil util=em.getEntityManagerFactory().getPersistenceUnitUtil();
        //then
        System.out.println(util.isLoaded(findMembers.get(0).getTeam()));
        assertThat(findMembers.size()).isEqualTo(4);
        for (Member findMember : findMembers) {
            System.out.println(findMember);
            System.out.println(findMember.getTeam());
        }

    }

    private Member createMember(String username, int age, Team teamA) {
        Member member = new Member(username, age, teamA);
        em.persist(member);
        return member;
    }

    private Team createTeam(String teamName) {
        Team team = new Team(teamName);
        em.persist(team);
        return team;
    }
}