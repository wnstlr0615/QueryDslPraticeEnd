package study.query.dsl.querydsl.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.query.dsl.querydsl.MemberSearchCond;
import study.query.dsl.querydsl.dto.MemberTeamDto;
import study.query.dsl.querydsl.entity.Member;
import study.query.dsl.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    public void before(){

        Team teamA = createTeam("teamA");
        Team teamB = createTeam("teamB");

        Member member1 = createMember("member1", 10, teamA);
        Member member2 = createMember("member2", 20, teamA);
        Member member3 = createMember("member3", 30, teamB);
        Member member4 = createMember("member4", 40, teamB);

    }

    @Test
    public void findAllTest() throws Exception{
        //given
        //when
        List<Member> members = memberJpaRepository.findAll_Querydsl();
        //then
        assertThat(members.size()).isEqualTo(4);

    }
    @Test
    public void findByUsername() throws Exception{
        //given
        //when
        List<Member> members = memberJpaRepository.findByUsername_Querydel("member1");
        //then
        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getName()).isEqualTo("member1");
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
    @Test
    public void memberSearchTest() throws Exception{
        //given
        MemberSearchCond cond = new MemberSearchCond();
        cond.setTeamName("teamA");

        //when
        List<MemberTeamDto> findMembers = memberJpaRepository.searchByBuilder(cond);

        //then
        for (MemberTeamDto findMember : findMembers) {
            System.out.println(findMember);
        }
    }
}