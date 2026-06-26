package com.keynor.rpg.domain.model;

public class PlayableCharacter {

    private final String name;
    private final Body body;
    private final Biomechanics biomechanics;
    private String loreReference;

    public PlayableCharacter(String name, Body body, Biomechanics biomechanics) {
        this.name = name;
        this.body = body;
        this.biomechanics = biomechanics;
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

    public Biomechanics getBiomechanics() {
        return biomechanics;
    }

    public String getLoreReference() {
        return loreReference;
    }
}
