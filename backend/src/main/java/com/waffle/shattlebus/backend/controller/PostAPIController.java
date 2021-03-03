package com.waffle.shattlebus.backend.controller;


import com.waffle.shattlebus.backend.model.Feedback;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PostAPIController {

    // 여긴 그,, 할게 없나?

    @PostMapping(path = "/feedback/")
    public Feedback postRequest(@RequestBody Feedback feedback){
        return feedback;
    }


}