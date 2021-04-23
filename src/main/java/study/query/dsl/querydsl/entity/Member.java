package study.query.dsl.querydsl.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "team")
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
        team=null;
    }

    public Member(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        if(team!=null){
            changeTeam(team);
        }
    }

    private void changeTeam(Team team) {
        if(this.team!=null){
            this.team.getMembers().remove(this);
        }
        this.team=team;
        team.getMembers().add(this);
    }
}
