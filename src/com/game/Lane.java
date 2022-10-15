package com.game;

import java.util.ArrayList;

public class Lane {

    int openIndex;
    ArrayList<Card> cards;

    public Lane(int initialSize){
        this.openIndex = initialSize - 1;
        this.cards = new ArrayList<>(initialSize);
    }

    public String getCardText(int index){
        if(this.getSize() <= index) return "";
        return index >= openIndex ? cards.get(index).toString() : "--";
    }

    public boolean isEmpty(){return this.getSize() == 0;}

    public Card getLastCard(){
        return this.cards.get(this.cards.size() - 1);
    }

    public Card getFirstOpenCard(){
        return this.cards.get(openIndex);
    }

    public int getSize(){return this.cards.size();}

    public Card getAndRemoveLastCard(){
        if(this.openIndex == this.getSize() - 1) this.openIndex --; // เช็คว่าต้องหงายไพ่เพิ่มมั้ยในเลน
        return this.cards.remove(this.getSize() - 1);
    }

    public void addCard(Card card){
        this.cards.add(card);
    }

    public void addCardsFromLane(Lane sourceLane){
        var cardsToAppend = new ArrayList<Card>();
        var initialSourceLaneSize = sourceLane.getSize();
        int indexOfSourceLane;
        for(indexOfSourceLane = sourceLane.openIndex; indexOfSourceLane < initialSourceLaneSize; indexOfSourceLane++){
            //find where to start removing
            if(this.isEmpty()) break;
            var targetCard = this.getLastCard();
            var sourceCard = sourceLane.cards.get(indexOfSourceLane);
            if((targetCard.value - sourceCard.value == 1) && (!targetCard.getColor().equals(sourceCard.getColor()))) break;
        }
        if(indexOfSourceLane == sourceLane.openIndex) sourceLane.openIndex -- ; //move the whole open portion
        while (indexOfSourceLane < sourceLane.getSize()) {
            cardsToAppend.add(sourceLane.cards.remove(indexOfSourceLane));
        }

        this.cards.addAll(cardsToAppend);
        if(this.openIndex < 0) this.openIndex = 0;
        if(sourceLane.openIndex < 0) sourceLane.openIndex = 0;
    }
}
