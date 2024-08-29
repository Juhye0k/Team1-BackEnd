package com.club_board.club_board_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity

@Setter
@Getter
public class Accession {

    @Id @GeneratedValue
    @Column(name="accession_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="club_id")
    private Club club;
    @Column(name = "role", columnDefinition = "VARCHAR(25) DEFAULT '비회원'")
    private String role="비회원";
}
