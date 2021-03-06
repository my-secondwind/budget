package ru.innopolis.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "currency")
@Data
@DynamicInsert
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyid;

    private String name;

    private String brief;
}
