package com.dubroushchyk.clever.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "company_user")
public class User extends BaseEntity<Long> {

    @Column(name = "login", nullable = false, unique = true)
    private String login;

}
