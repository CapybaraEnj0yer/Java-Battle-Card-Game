import java.util.ArrayList;

public abstract class Player {
    protected String name;
    protected int score;
    protected ArrayList<Card> hand;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void incrementScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        hand.clear();
        score = 0;
    }

    public boolean hasCards() {
        return !hand.isEmpty();
    }

    public abstract Card playTurn();
}