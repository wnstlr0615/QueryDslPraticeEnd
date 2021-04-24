package study.query.dsl.querydsl.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

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
    public void search() throws Exception{
        //given
        MemberSearchCond cond=new MemberSearchCond();
        cond.setTeamName("teamA");
        //when
        List<MemberTeamDto> findMembers = memberRepository.search(cond);
        //then
        assertThat(findMembers.size()).isEqualTo(2);
        assertThat(findMembers.get(0).getTeamName()).isEqualTo("teamA");
    }

    @Test
    public void pageSimpleSearch() throws Exception{
        //given
        MemberSearchCond cond=new MemberSearchCond();
        cond.setTeamName("teamA");
        //when
        Page<MemberTeamDto> memberTeamDtos = memberRepository.searchPageSimple(cond, PageRequest.of(0, 10));
        //then
        assertThat(memberTeamDtos.getSize()).isNotEqualTo(0);
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