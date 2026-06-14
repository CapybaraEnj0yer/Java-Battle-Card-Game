public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Card playTurn() {
        if (!hand.isEmpty()) {
            return hand.remove(0);
        }
        return null;
    }
}