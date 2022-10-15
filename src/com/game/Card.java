package com.game;

import com.game.constants.Suite;

public class Card {

    Suite suite;
    int value;
    String valueText;

    public Card(Suite suite, int value){
        this.suite = suite;
        this.value = value;
        this.valueText = switch (value) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> "" + value;
        };
    }

    public String toString(){
        return this.valueText + this.suite.text;
    }

    public String getColor(){
        return this.suite.getColor();
    }
}
