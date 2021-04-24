package study.query.dsl.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.query.dsl.querydsl.MemberSearchCond;
import study.query.dsl.querydsl.dto.MemberTeamDto;
import study.query.dsl.querydsl.dto.QMemberTeamDto;
import study.query.dsl.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.query.dsl.querydsl.entity.QMember.member;
import static study.query.dsl.querydsl.entity.QTeam.team;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public List<Member> findAll_Querydsl(){
        return query.selectFrom(member)
                .fetch();
    }
    public List<Member> findByUsername_Querydel(String name){
        return query.selectFrom(member)
                .where(member.name.eq(name))
                .fetch();
    }
    public List<MemberTeamDto> searchByBuilder(MemberSearchCond cond){
        BooleanBuilder builder=new BooleanBuilder();
        if(hasText(cond.getUsername()))builder.and(member.name.eq(cond.getUsername()));
        if(hasText(cond.getTeamName()))builder.and(member.team.name.eq(cond.getTeamName()));
        if(cond.getAgeGoe()!=null)builder.and(member.age.goe(cond.getAgeGoe()));
        if(cond.getAgeLoe()!=null)builder.and(member.age.loe(cond.getAgeLoe()));

        return query.select(new QMemberTeamDto(
                member.id, member.name, member.age
                , team.id, team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }
}
