import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card {
    private String suit;
    private int value; // 2-10, 11=J, 12=Q, 13=K, 14=A
    private String rank;
    private Image cardImage;

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
        setRank();
        loadImage(); // Load image when card is created
    }

    private void setRank() {
        if (value >= 2 && value <= 10) {
            this.rank = String.valueOf(value);
        } else if (value == 11)
            this.rank = "Jack";
        else if (value == 12)
            this.rank = "Queen";
        else if (value == 13)
            this.rank = "King";
        else if (value == 14)
            this.rank = "Ace";
    }

    private void loadImage() {
        // 1. Map full suit names to the single letter in filenames
        String suitChar = "";
        if (suit.equals("Hearts"))
            suitChar = "h";
        else if (suit.equals("Diamonds"))
            suitChar = "d";
        else if (suit.equals("Clubs"))
            suitChar = "c";
        else if (suit.equals("Spades"))
            suitChar = "s";

        // 2. Map values (14 for Ace) to filename values (1 for Ace)
        int fileVal = value;
        if (value == 14)
            fileVal = 1;

        // 3. Construct filename
        String filename = "images/" + fileVal + suitChar + ".png";

        // 4. Try to load and resize the image
        try {
            Image rawImage = ImageIO.read(new File(filename));
            // Resize to fit our 100x140 display area
            cardImage = rawImage.getScaledInstance(100, 140, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.out.println("Error loading card image: " + filename);
            cardImage = null;
        }
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public String toString() {
        return rank + " of " + suit;
    }

    // Draws the loaded card image
    public void draw(Graphics g, int x, int y) {
        if (cardImage != null) {
            // Draw the loaded image
            g.drawImage(cardImage, x, y, null);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, 100, 140);
        } else {
            // draw a red 'X' box so the game still runs in case image failed to load
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 100, 140);
            g.setColor(Color.RED);
            g.drawRect(x, y, 100, 140);
            g.drawLine(x, y, x + 100, y + 140);
            g.drawLine(x + 100, y, x, y + 140);
            g.drawString("IMG ERROR", x + 20, y + 70);
        }
    }
}