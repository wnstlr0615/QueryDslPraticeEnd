package study.query.dsl.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;
import study.query.dsl.querydsl.entity.Member;
import study.query.dsl.querydsl.entity.QMember;
import study.query.dsl.querydsl.entity.Team;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.query.dsl.querydsl.entity.QMember.member;
import static study.query.dsl.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    EntityManager em;
    JPAQueryFactory query;
    
    @BeforeEach
    public void before(){
        query=new JPAQueryFactory(em);
        
        Team teamA = createTeam("teamA");
        Team teamB = createTeam("teamB");

        Member member1 = createMember("member1", 10, teamA);
        Member member2 = createMember("member2", 20, teamA);
        Member member3 = createMember("member3", 30, teamB);
        Member member4 = createMember("member4", 40, teamB);

    }
    @Test
    public void startJPQL() throws Exception{
        //when
        Member findMember = em.createQuery("" +
                "select m from Member m " +
                "where m.name=:name" +
                "", Member.class)
                .setParameter("name", "member1")
                .getSingleResult();
        //then
        assertThat(findMember.getName()).isEqualTo("member1");
    }
    @Test
    public void startQuerydsl() throws Exception{

        //when
        QMember m = new QMember("m");
        Member findMember = query.selectFrom(m)
                .where(m.name.eq("member1"))
                .fetchOne();
        //then
        assertThat(findMember != null ? findMember.getName() : null).isEqualTo("member1");

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
    public void sort() throws Exception{
        //given
        createMember(null, 100, null);
        createMember("member5", 100, null);
        createMember("member6", 100, null);

        //when
        List<Member> findMembers = query.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.name.asc().nullsLast())
                .fetch();
        //then
        assertThat(findMembers.get(0).getName()).isEqualTo("member5");
        assertThat(findMembers.get(1).getName()).isEqualTo("member6");
        assertThat(findMembers.get(2).getName()).isNull();
        assertThat(findMembers.size()).isEqualTo(3);

    }
    @Test
    public void paging1() throws Exception{
        //given

        //when
        List<Member> findMembers = query.selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetch();
        //then
        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    public void paging2() throws Exception{
        //given

        //when
        QueryResults<Member> queryResults = query.selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        List<Member> findMembers = queryResults.getResults();
        //then

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(findMembers.size()).isEqualTo(2);
    }
    @Test
    public void aggregation() throws Exception{
        //given
        //when
        List<Tuple> result = query.select(member.count(), member.age.sum(), member.age.avg()
                , member.age.max(), member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
        //then
    }
    @Test
    public void group() throws Exception{
        //given
        //when
        List<Tuple> result = query.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);
        //then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }
    @Test
    public void join() throws Exception{
        //given
        //when
        List<Member> findMembers = query.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        //then
        assertThat(findMembers)
                .extracting("name")
                .containsExactly("member1", "member2");
    }
    @Test
    public void theta_join() throws Exception{
        //given
        createMember("teamA", 10, null);
        createMember("teamB", 10, null);

        //when
        List<Member> findMembers = query.select(member)
                .from(member, team)
                .where(member.name.eq(team.name))
                .fetch();

        //then
        assertThat(findMembers).extracting("name")
                .containsExactly("teamA", "teamB");
    }
    @Test
    public void join_on_filtering() throws Exception{
        //given

        //when
        List<Tuple> teamA = query.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();
        //then
        assertThat(teamA.size()).isEqualTo(4);
        assertThat(teamA.get(0).get(team).getName()).isEqualTo("teamA");
    }
    @Test
    public void join_on_no_relation() throws Exception{
        //given
        createMember("teamA", 10, null);
        createMember("teamB", 10, null);
        //when
        List<Tuple> result = query.select(member, team)
                .from(member)
                .leftJoin(team).on(member.name.eq(team.name))
                .fetch();
        //then
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    @Test
    public void fetchJoinNo() throws Exception{
        //given
            em.flush();
            em.clear();
        //when
        Member findMember = query.selectFrom(member)
                .join(member.team, team)
                .where(member.name.eq("member1"))
                .fetchOne();

        //then
        PersistenceUtil util=em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(findMember.getTeam())).as("페치 조인 미적용").isFalse();

    }
    @Test
    public void fetchJoin() throws Exception{
        //given
        em.flush();
        em.clear();
        //when
        Member findMember = query.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.name.eq("member1"))
                .fetchOne();

        //then
        PersistenceUtil util=em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(findMember.getTeam())).as("페치 조인 적용").isTrue();

    }
    @Test
    public void subQuery() throws Exception{
        //given
        QMember memberSub = new QMember("memberSub");
        //when
        List<Member> result = query.selectFrom(member)
                .where(
                        member.age.eq(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub)
                        )
                ).fetch();
        //then
        assertThat(result).extracting("age").containsExactly(40);
    }
    @Test
    public void subQueryGoe () throws Exception{
        //given
        QMember subMember = new QMember("subMember");
        //when
        List<Member> result = query.selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(subMember.age.avg())
                                .from(subMember)
                )).fetch();
        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("age").containsExactly(30,40);
    }
    @Test
    public void subQueryIn() throws Exception{
        //given
        QMember memberSub = new QMember("memberSub");
        //when
        List<Member> result = query.selectFrom(member)
                .where(member.age.in(JPAExpressions
                        .select(memberSub.age).from(memberSub)
                        .where(memberSub.age.gt(10))
                )).fetch();
        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("age").containsExactly(20,30,40);
    }
    @Test
    public void subQuerySelect() throws Exception{
        //given
        QMember memberSub = new QMember("memberSub");
        //when
        List<Tuple> result = query.select(member.name
                , JPAExpressions.select(memberSub.age.avg())
                .from(memberSub)
        )
                .from(member)
                .fetch();
        //then
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    @Test
    public void querydslCase() throws Exception{
        //given
        //when
        List<Tuple> result = query.select(
                member.name, member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
        ).from(member)
                .fetch();
        //then
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    @Test
    public void querydslCaseHard() throws Exception{
        //given
        //when
        List<Tuple> result = query.select(member.name,
                new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(20, 40)).then("20~40살")
                        .otherwise("기타")

        ).from(member)
                .fetch();
        //then
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    /**
    *
    * 예를 들어서 다음과 같은 임의의 순서로 회원을 출력하고 싶다면?
     * * 1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
2. 0 ~ 20살 회원 출력
3. 21 ~ 30살 회원 출력
**/
    @Test
    @Description("case문 +orderBy 연습문제")
    public void caseOrderByEx1() throws Exception{
        //given

        //when
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = query.select(member.name, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();
        //then
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
    }
    @Test
    public void () throws Exception{
        //given
            
        //when
        
        //then
    }
}
