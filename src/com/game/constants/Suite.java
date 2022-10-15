package com.game.constants;

public enum Suite {
    Clubs("C"),
    Diamonds("D"),
    Hearts("H"),
    Spades("S");

    public final String text;

    Suite(String text) {
        this.text = text;
    }

    public String getColor(){
        return (this.equals(Clubs) || this.equals(Spades)) ? "Black" : "Red";
    }
}
