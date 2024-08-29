package com.club_board.club_board_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter

public class User implements UserDetails {

    @Id @GeneratedValue
    @Column(name="user_id")
    private Long id;
    private String username;
    private String password;
    private String department;
    private String student_id;
    private int grade;
    private String phoneNumber;
    private boolean is_department_public=true;
    private boolean is_grade_public=true;
    private boolean is_phone_public=false;
    private LocalDate registrationDate;
    private LocalDate lastLoginDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Accession> accessions = new HashSet<>();

    public void addAccession(Accession accession) {
        this.accessions.add(accession);
        accession.setUser(this);
    }

    @Builder
    public User(String username,String password,String department, String student_id,int grade,String phoneNumber,LocalDate registrationDate)
    {
        this.username = username;
        this.password = password;
        this.department = department;
        this.student_id = student_id;
        this.grade = grade;
        this.phoneNumber = phoneNumber;
        this.registrationDate=registrationDate;

    }




    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
