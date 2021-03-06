package ru.innopolis.domain;



import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@DynamicInsert
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

    private String login;

    private String password;

    private String email;

    private Integer isblock;

    @ManyToOne
    @JoinColumn(name = "roleid")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private Famem famem;

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", isblock=" + isblock +
                ", role=" + role +
                '}';
    }
}