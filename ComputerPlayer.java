public class ComputerPlayer extends Player {

    public ComputerPlayer() {
        super("Computer");
    }

    @Override
    public Card playTurn() {
        if (!hand.isEmpty()) {
            return hand.remove(0);
        }
        return null;
    }
}