package study.query.dsl.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.query.dsl.querydsl.MemberSearchCond;
import study.query.dsl.querydsl.dto.MemberTeamDto;
import study.query.dsl.querydsl.repository.MemberJpaRepository;
import study.query.dsl.querydsl.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond){
        return memberJpaRepository.searchByBuilder(cond);
    }
    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMember2(MemberSearchCond cond, Pageable pageable){
        return memberRepository.searchPageSimple(cond, pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCond cond, Pageable pageable){
        return memberRepository.searchPageComplex(cond, pageable);
    }
}
