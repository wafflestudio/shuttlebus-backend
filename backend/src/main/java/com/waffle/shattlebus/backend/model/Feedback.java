package com.waffle.shattlebus.backend.model;

import lombok.Data;

@Data
public class Feedback {

    private String id;
    private int date;
    private String type;
    private String content;
    private boolean is_solved;
}
