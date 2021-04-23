package study.query.dsl.querydsl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
public class Hello {
    @Id
    @GeneratedValue
    private Long id;

}
