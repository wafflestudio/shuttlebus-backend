package com.waffle.shattlebus.backend.controller;


import com.waffle.shattlebus.backend.model.Feedback;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PostAPIController {

    // http통신할때 post는 body에다 data를 집어넣어서 받아오겠다.
    // @RequestBody에 SearchVO에 있는 값을 매칭해서 보내줘라.

    @PostMapping(path = "/feedback/")
    public Feedback postRequest(@RequestBody Feedback feedback){
        return feedback;
    }


}