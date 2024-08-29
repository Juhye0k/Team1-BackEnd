package com.club_board.club_board_server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ClubCategory {

    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    @Column(name="category_name")
    private String categoryName;
}
