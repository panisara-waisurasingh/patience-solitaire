package com.game;

import com.game.constants.Suite;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        //initial setup (create deck, lanes, suites)
        var deck = createAndShuffleDeck(); // create shuffled deck --> will be the main draw deck
        // คว่ำกอง index 0 = top deck (actually it's up to you)
        var pile = new Stack<Card>();
        var suites = createSuitePiles(); // set suites to be clubs, diamonds, hearts, and spades in order
        var lanes = createAndPopulateLanes(deck);

        /*
        GAME PLAY
        loop{
            - check win condition --> if win, congrats & close game
            - get valid move (Q, D is here)
            - validate that move is playable
            - make the move
            - update states & print new screen
        }
        */

        while (true) {
            printGameScreen(deck, pile, suites, lanes);
            if (metWinConditions(suites)) {
                System.out.println("You win! Thank you for playing.");
                return;
            }
            var move = getValidInput(pile, suites, lanes);
            if (move.equals("Q")) {
                System.out.println("Thank you for playing.");
                return;
            }
            if (move.equals("D")) {
                drawCardFromDrawPile(deck, pile);
            } else {
                playTheMove(pile, suites, lanes, move);
            }
        }
    }

    private static Stack<Card> createAndShuffleDeck() {
        var deck = createDeck();
        return shuffle(deck);
    }

    private static ArrayList<Card> createDeck() {
        var deck = new ArrayList<Card>();
        for (var suite : Suite.values()) {
            for (var i = 1; i <= 13; i++) {
                deck.add(new Card(suite, i));
            }
        }
        return deck;
    }

    private static Stack<Card> shuffle(List<Card> deck) {
        var shuffledDeck = new Stack<Card>();
        Random random = new Random();
        for (int i = deck.size(); i > 0; i--) {
            shuffledDeck.add(deck.remove(random.nextInt(i)));
        }
        return shuffledDeck;
    }

    private static ArrayList<Lane> createAndPopulateLanes(Stack<Card> deck) {
        var lanes = new ArrayList<Lane>(7);
        for (int i = 1; i <= 7; i++) {
            var lane = new Lane(i);
            populateLane(lane, deck, i);
            lanes.add(lane);
        }
        return lanes;
    }

    private static void populateLane(Lane lane, Stack<Card> deck, int amount) {
        for (int i = 0; i < amount; i++) {
            lane.cards.add(deck.pop());
        }
    }

    private static ArrayList<Stack<Card>> createSuitePiles() {
        var suites = new ArrayList<Stack<Card>>();
        for (int i = 0; i < 4; i++) suites.add(new Stack<>());
        return suites;
    }

    private static void printGameScreen(Stack<Card> deck, Stack<Card> pile, ArrayList<Stack<Card>> suites, ArrayList<Lane> lanes) {
        newIntelliJScreen();
        printTopSection(deck, pile, suites);
        printLanes(lanes);
    }

    private static void newIntelliJScreen() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private static void printTopSection(Stack<Card> deck, Stack<Card> pile, ArrayList<Stack<Card>> suites) {
        //print first row
        System.out.println("\t\t\tC\tD\tH\tS");

        //print second row
        StringBuilder text = new StringBuilder();
        text.append(deck.empty() ? "  " : "--");
        text.append("\t");
        text.append(pile.empty() ? "  " : pile.peek().toString());
        text.append("\t\t");
        for (var suite : suites) text.append(suite.empty() ? "  " : suite.peek().toString()).append("\t");
        System.out.println(text);
        System.out.println("1\t2\t3\t4\t5\t6\t7");
    }

    private static void printLanes(ArrayList<Lane> lanes) {
        // find how many rows needed --> find lane with max no. of cards
        int rows = getBiggestLaneSize(lanes);

        //print each row
        for (int i = 0; i < rows; i++) {
            for (var lane : lanes) {
                System.out.print(lane.getCardText(i) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static int getBiggestLaneSize(ArrayList<Lane> lanes) {
        int maxSize = -1;
        for (Lane lane : lanes) {
            if (lane.getSize() > maxSize) {
                maxSize = lane.getSize();
            }
        }
        return maxSize;
    }

    private static boolean metWinConditions(ArrayList<Stack<Card>> suites) {
        for (var suite : suites) {
            if (suite.empty()) return false;
            if (!suite.peek().valueText.equals("K")) return false;
        }
        return true;
    }

    private static String getValidInput(Stack<Card> pile, ArrayList<Stack<Card>> suites, ArrayList<Lane> lanes) {
        System.out.println("Please type your next move in format \"lane lane\", \"lane suite\", \"suite lane\", \"pile lane\" or \"pile suite\".");
        System.out.println("Lane can be from 1 to 7. Suite can be C, D, H or S. Pile is P");
        System.out.println("(D to Draw and Q to Quit)");
        Scanner sc = new Scanner(System.in);
        String move;
        while (true) {
            System.out.print("move: ");
            move = sc.nextLine().trim();
            if (!isMoveCorrectFormat(move)) {
                System.out.println("Error: Move \"" + move + "\" is in incorrect format");
                continue;
            }
            if (!isMoveValid(move, pile, suites, lanes)) {
                System.out.println("Error: Cannot make move \"" + move + "\"");
                continue;
            }
            return move;
        }
    }

    private static boolean isMoveCorrectFormat(String move) {
        if (move.equals("D") || move.equals("Q")) return true;
        //Example of correct format: 1 C (lane 1 to clubs pile), 7 3 (lane 7 to lane 3), P 2 (drawn pile to lane 2)
        if (move.length() != 3) return false;
        List<Character> lanesAndSuites = List.of('1', '2', '3', '4', '5', '6', '7', 'C', 'D', 'H', 'S', 'P');
        return lanesAndSuites.contains(move.charAt(0)) && lanesAndSuites.contains(move.charAt(2));
    }

    private static boolean isMoveValid(String move, Stack<Card> pile, ArrayList<Stack<Card>> suites, ArrayList<Lane> lanes) {
        if (move.equals("D") || move.equals("Q")) return true;
        String source = move.substring(0, 1);
        String destination = move.substring(2);
        List<String> suiteNames = List.of("C", "D", "H", "S");
        /*
        Possible Move:
        Pile -> Lane
        Pile -> Suite
        Lane -> Lane
        Lane -> Suite
        Suite -> Lane
         */

        // Catch obvious invalid moves first
        // Any -> Pile
        if (destination.equals("P")) return false;
        // Same Lane or Same Suite
        if (source.equals(destination)) return false;
        // Suite A -> Suite B
        if (suiteNames.contains(source) && suiteNames.contains(destination)) return false;

        //Main logic
        if (suiteNames.contains(destination)) {
            //destination is suite pile
            var destinationSuite = suites.get(suiteNames.indexOf(destination));
            if (source.equals("P")) return validateMovePileToSuite(pile, destinationSuite, destination);
            else {
                var lane = lanes.get(Integer.parseInt(source) - 1); //get source lane
                return validateMoveLaneToSuite(lane, destinationSuite, destination);
            }
        } else {
            //destination is lane
            var destinationLane = lanes.get(Integer.parseInt(destination) - 1);
            if (source.equals("P")) {
                //source is pile
                return validateMovePileToLane(pile, destinationLane);
            } else if (suiteNames.contains(source)) {
                //source is suite
                var sourceSuite = suites.get(suiteNames.indexOf(source));
                return validateMoveSuiteToLane(sourceSuite, destinationLane);
            } else {
                //source is lane
                var sourceLane = lanes.get(Integer.parseInt(source) - 1); //get source lane
                return validateMoveLaneToLane(sourceLane, destinationLane);

            }
        }
    }

    private static boolean validateMovePileToSuite(Stack<Card> pile, Stack<Card> destinationSuite, String suiteName) {
        if (pile.empty()) return false;

        var topPileCard = pile.peek();
        if (!topPileCard.suite.text.equals(suiteName)) return false;
        if (destinationSuite.empty()) return topPileCard.value == 1;

        var topSuiteCard = destinationSuite.peek();
        return topPileCard.value - topSuiteCard.value == 1;
    }

    private static boolean validateMoveLaneToSuite(Lane lane, Stack<Card> destinationSuite, String suiteName) {
        if (lane.isEmpty()) return false;

        var lastLaneCard = lane.getLastCard();
        if (!lastLaneCard.suite.text.equals(suiteName)) return false;
        if (destinationSuite.empty()) return lastLaneCard.value == 1;

        var topSuiteCard = destinationSuite.peek();
        return lastLaneCard.value - topSuiteCard.value == 1;
    }

    private static boolean validateMovePileToLane(Stack<Card> pile, Lane lane) {
        if (pile.empty()) return false;
        var topPileCard = pile.peek();
        if (lane.isEmpty()) return topPileCard.value == 13;
        var lastLaneCard = lane.getLastCard();
        return (lastLaneCard.value - topPileCard.value == 1) && (!lastLaneCard.getColor().equals(topPileCard.getColor()));
    }

    private static boolean validateMoveSuiteToLane(Stack<Card> suite, Lane lane) {
        if (suite.empty()) return false;
        var topSuiteCard = suite.peek();
        if (lane.isEmpty()) return topSuiteCard.value == 13;
        var lastLaneCard = lane.getLastCard();
        return (lastLaneCard.value - topSuiteCard.value == 1) && (!lastLaneCard.getColor().equals(topSuiteCard.getColor()));
    }

    private static boolean validateMoveLaneToLane(Lane sourceLane, Lane destinationLane) {
        if (sourceLane.isEmpty()) return false;
        var sourceCard = sourceLane.getFirstOpenCard();
        if (destinationLane.isEmpty()) return sourceCard.value == 13;
        else {
            var targetCard = destinationLane.getLastCard();
            return validateSourceLaneWithTargetCard(sourceLane, targetCard);
        }
    }

    private static boolean validateSourceLaneWithTargetCard(Lane sourceLane, Card targetCard) {
        for(int i = sourceLane.openIndex; i < sourceLane.getSize(); i++){
            var sourceCard = sourceLane.cards.get(i);
            if ((targetCard.value - sourceCard.value == 1) && (!targetCard.getColor().equals(sourceCard.getColor()))) return true; //เอาใบนี้ต่อได้
        }
        return false;
    }

    private static void drawCardFromDrawPile(Stack<Card> deck, Stack<Card> pile) {
        if (deck.empty() && pile.empty()) return;
        if (deck.empty()) {
            transferFromPileToDeck(deck, pile);
            return;
        }
        pile.add(deck.pop());
    }

    private static void transferFromPileToDeck(Stack<Card> deck, Stack<Card> pile) {
        //Assumption: to get to this code block, deck must be empty and pile must NOT be empty.
        while (!pile.empty()) {
            deck.add(pile.pop());
        }
    }

    private static void playTheMove(Stack<Card> pile, ArrayList<Stack<Card>> suites, ArrayList<Lane> lanes, String move) {
        //Assumption: move is already validated and can always play the move
        var source = move.substring(0, 1);
        var destination = move.substring(2);
        List<String> suiteNames = List.of("C", "D", "H", "S");
        if (suiteNames.contains(destination)) {
            //destination is suite
            var destinationSuite = suites.get(suiteNames.indexOf(destination));
            if(source.equals("P")){
                //source is pile
                destinationSuite.add(pile.pop());
            } else {
                //source is lane
                var sourceLane = lanes.get(Integer.parseInt(source) - 1);
                destinationSuite.add(sourceLane.getAndRemoveLastCard());
            }

        } else {
            //destination is lane
            var destinationLane = lanes.get(Integer.parseInt(destination) - 1);
            if (source.equals("P")) {
                //source is pile
                destinationLane.addCard(pile.pop());
            } else if (suiteNames.contains(source)) {
                //source is suite
                var sourceSuite = suites.get(suiteNames.indexOf(source));
                destinationLane.addCard(sourceSuite.pop());
            } else {
                //source is lane
                var sourceLane = lanes.get(Integer.parseInt(source) - 1); //get source lane
                destinationLane.addCardsFromLane(sourceLane);
            }
        }
    }

}
