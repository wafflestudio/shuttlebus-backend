package com.waffle.shattlebus.backend.Exception;

public class NotFoundException extends Exception {

    public NotFoundException(){
        super();
    }

    public NotFoundException(String msg){
        super(msg);
    }
}
