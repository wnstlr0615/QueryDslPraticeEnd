package study.query.dsl.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.query.dsl.querydsl.MemberSearchCond;
import study.query.dsl.querydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberCustomRepository {
    List<MemberTeamDto> search(MemberSearchCond cond);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCond cond, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable);
    Page<MemberTeamDto> searchPageCountQuery(MemberSearchCond cond, Pageable pageable);
}
