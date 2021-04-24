package study.query.dsl.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import study.query.dsl.querydsl.MemberSearchCond;
import study.query.dsl.querydsl.dto.MemberTeamDto;
import study.query.dsl.querydsl.dto.QMemberTeamDto;
import study.query.dsl.querydsl.entity.Member;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;
import static study.query.dsl.querydsl.entity.QMember.member;
import static study.query.dsl.querydsl.entity.QTeam.team;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {
    final JPAQueryFactory query;

    @Override
    public List<MemberTeamDto> search(MemberSearchCond cond) {
        return query.select(new QMemberTeamDto(
                member.id, member.name, member.age
                , team.id, team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe())
                )
                .fetch();

    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCond cond, Pageable pageable) {
        QueryResults<MemberTeamDto> results = query
                .select(new QMemberTeamDto(
                        member.id,
                        member.name,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .leftJoin(member.team, team)
                .fetchResults();
        List<MemberTeamDto> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);

    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable) {
        List<MemberTeamDto> content = query.select(new QMemberTeamDto(
                member.id, member.name, member.age
                , team.id, team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = query.selectFrom(member)
                .leftJoin(member.team, team)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe()))
                .fetchCount();
        return new PageImpl(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageCountQuery(MemberSearchCond cond, Pageable pageable) {
        List<MemberTeamDto> content = query.select(new QMemberTeamDto(
                member.id, member.name, member.age
                , team.id, team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> countQuery = query.selectFrom(member)
                .leftJoin(member.team, team)
                .where(usernameEq(cond.getUsername())
                        , teamNameEq(cond.getTeamName())
                        , ageLoe(cond.getAgeLoe())
                        , ageGoe(cond.getAgeGoe())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression usernameEq(String username) {

        return !hasLength(username) ? null : member.name.eq(username);

    }

    private BooleanExpression teamNameEq(String teamName) {

        return !hasLength(teamName) ? null : team.name.eq(teamName);

    }

    private BooleanExpression ageGoe(Integer ageGoe) {

        return ageGoe == null ? null : member.age.goe(ageGoe);

    }

    private BooleanExpression ageLoe(Integer ageLoe) {

        return ageLoe == null ? null : member.age.loe(ageLoe);

    }

}
