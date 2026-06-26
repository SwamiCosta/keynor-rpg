package com.keynor.rpg.domain.model;

public class PlayableCharacter {

    private final String name;
    private final Body body;
    private String loreReference;

    public PlayableCharacter(String name, Body body) {
        this.name = name;
        this.body = body;
    }

    public void linkToLore(String loreReference) {
        this.loreReference = loreReference;
    }

    public String getName() {
        return name;
    }

    public Body getBody() {
        return body;
    }

    public String getLoreReference() {
        return loreReference;
    }
}
