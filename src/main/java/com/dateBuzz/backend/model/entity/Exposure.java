package com.dateBuzz.backend.model.entity;

public enum Exposure {
    OPEN, CLOSE;

    public static Exposure returnExposure(String exposure){
        if(exposure.equals("OPEN")) return Exposure.OPEN;
        return Exposure.CLOSE;
    }
}
