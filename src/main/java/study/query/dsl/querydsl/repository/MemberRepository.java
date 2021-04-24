package study.query.dsl.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.query.dsl.querydsl.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberCustomRepository{

 }
