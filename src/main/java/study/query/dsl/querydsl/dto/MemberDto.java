package study.query.dsl.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Data
@NoArgsConstructor
public class MemberDto {
    private String name;
    private int age;
    @QueryProjection
    public MemberDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
