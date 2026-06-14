import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GameManager {
    private Deck deck;
    private Player p1;
    private Player p2;
    private String lastResult;
    private int roundNumber;

    public GameManager(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.deck = new Deck();
        this.lastResult = "Welcome to Card Battle!";
        this.roundNumber = 0;
    }

    public void startNewGame() {
        deck.reset();
        deck.shuffle();
        p1.reset();
        p2.reset();
        roundNumber = 0;
        lastResult = "Deck Shuffled. Ready to play!";

        while (deck.cardsLeft() > 0) {
            p1.addCard(deck.drawCard());
            p2.addCard(deck.drawCard());
        }
    }

    public Card getPlayerCard() {
        return p1.playTurn();
    }

    public Card getComputerCard() {
        return p2.playTurn();
    }

    public String resolveRound(Card c1, Card c2) {
        roundNumber++;

        String roundLog = "Round " + roundNumber + ": " + p1.getName() + " plays " + c1.toString() + ", " + p2.getName()
                + " plays " + c2.toString();

        if (c1.getValue() > c2.getValue()) {
            int points = 1;
            if (c1.getValue() == 14)
                points = 5;
            p1.incrementScore(points);
            lastResult = "Winner: " + p1.getName() + " (+" + points + " pts)";
        } else if (c2.getValue() > c1.getValue()) {
            int points = 1;
            if (c2.getValue() == 14)
                points = 5;
            p2.incrementScore(points);
            lastResult = "Winner: " + p2.getName() + " (+" + points + " pts)";
        } else {
            lastResult = "It's a Tie! No points.";
        }

        return roundLog + "\n" + lastResult;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getCardsRemaining() {
        return 26 - roundNumber;
    }

    public String getLastResult() {
        return lastResult;
    }

    public boolean isGameOver() {
        return !p1.hasCards();
    }

    public String getWinner() {
        if (p1.getScore() > p2.getScore())
            return p1.getName();
        else if (p2.getScore() > p1.getScore())
            return p2.getName();
        else
            return "Draw";
    }

    // Save Score with Date
    public void saveScore(String name, int score) {
        try {
            FileWriter fw = new FileWriter("highscores.txt", true);
            // Format: 'Name: Score: Date'
            fw.write(name + ": " + score + ": " + LocalDate.now() + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving score");
        }
    }

    public String getHighScores() {
        ArrayList<ScoreRecord> scoreList = new ArrayList<>();

        try {
            File f = new File("highscores.txt");
            if (f.exists()) {
                Scanner sc = new Scanner(f);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    try {
                        String[] parts = line.split(": ");
                        if (parts.length >= 2) {
                            String name = parts[0];
                            int s = Integer.parseInt(parts[1].trim());
                            String d = (parts.length > 2) ? parts[2] : "N/A";
                            scoreList.add(new ScoreRecord(name, s, d));
                        }
                    } catch (Exception e) {
                    }
                }
                sc.close();
            } else {
                return "No high scores yet.";
            }
        } catch (FileNotFoundException e) {
            return "Error loading scores.";
        }

        Collections.sort(scoreList);

        StringBuilder sb = new StringBuilder();
        sb.append("TOP SCORES:\n----------------\n");
        int rank = 1;
        for (ScoreRecord r : scoreList) {
            sb.append(rank + ". " + r.name + " - " + r.score + " pts (" + r.date + ")\n");
            rank++;
            if (rank > 10)
                break;
        }
        return sb.toString();
    }

    private class ScoreRecord implements Comparable<ScoreRecord> {
        String name;
        int score;
        String date;

        public ScoreRecord(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }

        @Override
        public int compareTo(ScoreRecord other) {
            return other.score - this.score;
        }
    }
}