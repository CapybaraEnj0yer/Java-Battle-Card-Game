import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        reset();
    }

    public void reset() {
        cards.clear();
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };

        // create 52 cards
        for (String s : suits) {
            for (int v = 2; v <= 14; v++) {
                cards.add(new Card(s, v));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public int cardsLeft() {
        return cards.size();
    }
}