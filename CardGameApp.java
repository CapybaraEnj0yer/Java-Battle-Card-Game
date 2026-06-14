import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent; // Needed for Keyboard Input

public class CardGameApp extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    GameManager gameManager;
    HumanPlayer human;
    ComputerPlayer computer;

    // GAME SCREEN COMPONENTS
    JLabel lblScoreP1, lblScoreP2;
    JLabel lblRoundInfo;
    JLabel lblTurnIndicator;
    JTextArea txtLog;
    DrawPanel cardVisualPanel;
    JButton btnPlay;

    // GAME OVER SCREEN COMPONENTS
    JLabel lblGameOverTitle;
    JLabel lblFinalWinner;
    JLabel lblFinalScore;

    // LOGIC VARIABLES
    Card currentP1Card = null;
    Card currentP2Card = null;
    Timer turnTimer;
    boolean isTurnInProgress = false; // is used to prevent spamming buttons

    public CardGameApp() {
        setTitle("Card Battle Project (ID: 340)");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        human = new HumanPlayer("Player");
        computer = new ComputerPlayer();
        gameManager = new GameManager(human, computer);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMenuPanel(), "MENU");
        mainPanel.add(createGamePanel(), "GAME");
        mainPanel.add(createGameOverPanel(), "GAMEOVER");

        add(mainPanel);

        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Map the SPACE key to the "playTurn" action map key
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "playTurn");

        mainPanel.getActionMap().put("playTurn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only allows spacebar if we are in the GAME screen and turn isn't locked
                if (btnPlay.isVisible() && btnPlay.isEnabled()) {
                    handleTurnPhase1();
                }
            }
        });
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 168, 82));

        JLabel title = new JLabel("JAVA CARD BATTLE");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);

        JButton btnStart = new JButton("Start New Game");
        JButton btnRules = new JButton("Game Rules");
        JButton btnScores = new JButton("High Scores");
        JButton btnExit = new JButton("Exit");

        JButton[] buttons = { btnStart, btnRules, btnScores, btnExit };
        for (JButton b : buttons) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setFont(new Font("Arial", Font.PLAIN, 18));
            b.setMaximumSize(new Dimension(250, 40));
        }

        btnStart.addActionListener(e -> {
            gameManager.startNewGame();
            updateStats();
            currentP1Card = null;
            currentP2Card = null;
            txtLog.setText("Game Started! Press SPACE or Click Play.\n");
            lblTurnIndicator.setText("Turn: PLAYER");
            cardVisualPanel.repaint();
            cardLayout.show(mainPanel, "GAME");
            btnPlay.setEnabled(true); // Ensure button is active
        });

        btnRules.addActionListener(e -> {
            String rules = "RULES:\n1. Play a card (Click or Spacebar).\n2. Higher value wins.\n3. Aces = 5 pts.";
            JOptionPane.showMessageDialog(this, rules, "Rules", JOptionPane.INFORMATION_MESSAGE);
        });

        btnScores.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, gameManager.getHighScores(), "High Scores",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        btnExit.addActionListener(e -> System.exit(0));

        panel.add(Box.createVerticalStrut(80));
        panel.add(title);
        panel.add(Box.createVerticalStrut(60));
        panel.add(btnStart);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRules);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnScores);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnExit);

        return panel;
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        topPanel.setBackground(Color.LIGHT_GRAY);

        lblScoreP1 = new JLabel(" Player: 0");
        lblScoreP2 = new JLabel(" Computer: 0");
        lblRoundInfo = new JLabel(" Round: 1 | Cards Left: 26");
        lblTurnIndicator = new JLabel(" Turn: WAITING");

        lblScoreP1.setFont(new Font("Arial", Font.BOLD, 16));
        lblScoreP2.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurnIndicator.setForeground(Color.BLUE);

        topPanel.add(lblScoreP1);
        topPanel.add(lblScoreP2);
        topPanel.add(lblRoundInfo);
        topPanel.add(lblTurnIndicator);

        panel.add(topPanel, BorderLayout.NORTH);

        cardVisualPanel = new DrawPanel();
        panel.add(cardVisualPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        txtLog = new JTextArea(4, 40);
        txtLog.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtLog);
        bottomPanel.add(scroll, BorderLayout.CENTER);

        btnPlay = new JButton("PLAY CARD (SPACE)");
        btnPlay.setFont(new Font("Arial", Font.BOLD, 22));
        btnPlay.setBackground(Color.ORANGE);

        turnTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTurnPhase2();
                turnTimer.stop();
            }
        });

        btnPlay.addActionListener(e -> handleTurnPhase1());

        bottomPanel.add(btnPlay, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGameOverPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);

        lblGameOverTitle = new JLabel("GAME OVER");
        lblGameOverTitle.setFont(new Font("Arial", Font.BOLD, 48));
        lblGameOverTitle.setForeground(Color.RED);
        lblGameOverTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblFinalWinner = new JLabel("Winner: ???");
        lblFinalWinner.setFont(new Font("Arial", Font.BOLD, 24));
        lblFinalWinner.setForeground(Color.WHITE);
        lblFinalWinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblFinalScore = new JLabel("Final Score: 0 - 0");
        lblFinalScore.setFont(new Font("Arial", Font.PLAIN, 18));
        lblFinalScore.setForeground(Color.LIGHT_GRAY);
        lblFinalScore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRestart = new JButton("Restart Game");
        JButton btnMenu = new JButton("Return to Main Menu");

        btnRestart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRestart.addActionListener(e -> {
            gameManager.startNewGame();
            updateStats();
            currentP1Card = null;
            currentP2Card = null;
            txtLog.setText("Game Restarted!\n");
            cardVisualPanel.repaint();
            cardLayout.show(mainPanel, "GAME");
            btnPlay.setEnabled(true);
        });

        btnMenu.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        panel.add(Box.createVerticalStrut(100));
        panel.add(lblGameOverTitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(lblFinalWinner);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblFinalScore);
        panel.add(Box.createVerticalStrut(50));
        panel.add(btnRestart);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnMenu);

        return panel;
    }

    // PHASE 1: Player Plays, Animation Starts
    private void handleTurnPhase1() {
        if (!gameManager.isGameOver() && !isTurnInProgress) {
            isTurnInProgress = true;
            btnPlay.setEnabled(false); // Disable button to prevent spamming

            lblTurnIndicator.setText("Turn: COMPUTER THINKING...");

            // 1. Get Player Card
            if (human.hasCards()) {
                currentP1Card = gameManager.getPlayerCard();
            }
            // 2. Clear Computer Card
            currentP2Card = null;

            cardVisualPanel.repaint();

            // 3. Start Timer (Wait 1 second before computer plays)
            turnTimer.start();
        }
    }

    // PHASE 2: Computer Plays, Result Calculated
    private void handleTurnPhase2() {
        if (computer.hasCards()) {
            currentP2Card = gameManager.getComputerCard();
        }

        String result = gameManager.resolveRound(currentP1Card, currentP2Card);
        txtLog.append(result + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());

        updateStats();
        cardVisualPanel.repaint();

        if (gameManager.isGameOver()) {
            showGameOverScreen();
        } else {
            lblTurnIndicator.setText("Turn: PLAYER");
            btnPlay.setEnabled(true); // Re-enable button
            isTurnInProgress = false;
        }
    }

    private void updateStats() {
        lblScoreP1.setText(" Player: " + human.getScore());
        lblScoreP2.setText(" Computer: " + computer.getScore());
        lblRoundInfo.setText(
                " Round: " + gameManager.getRoundNumber() + " | Cards Left: " + gameManager.getCardsRemaining());
    }

    private void showGameOverScreen() {
        String winner = gameManager.getWinner();

        int p1Score = human.getScore();
        int p2Score = computer.getScore();

        if (p1Score > p2Score) {
            lblGameOverTitle.setText("YOU WIN!");
            lblGameOverTitle.setForeground(Color.GREEN);
        } else if (p1Score < p2Score) {
            lblGameOverTitle.setText("GAME OVER");
            lblGameOverTitle.setForeground(Color.RED);
        } else {
            lblGameOverTitle.setText("IT'S A DRAW!");
            lblGameOverTitle.setForeground(Color.YELLOW);
        }

        lblFinalWinner.setText("Winner: " + winner);
        lblFinalScore.setText("Score: " + p1Score + " - " + p2Score);

        gameManager.saveScore(human.getName(), p1Score);

        cardLayout.show(mainPanel, "GAMEOVER");
    }

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(34, 139, 34));

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int cardW = 100;
            int cardH = 140;
            int gap = 40;
            int startY = (panelHeight - cardH) / 2;
            int p1X = (panelWidth / 2) - cardW - gap;
            int p2X = (panelWidth / 2) + gap;

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            String p1Label = "YOU";
            int p1LabelX = p1X + (cardW / 2) - (g.getFontMetrics().stringWidth(p1Label) / 2);
            g.drawString(p1Label, p1LabelX, startY - 20);

            if (currentP1Card != null) {
                currentP1Card.draw(g, p1X, startY);
            } else {
                g.drawRect(p1X, startY, cardW, cardH);
            }

            String vsLabel = "VS";
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.ORANGE);
            int vsWidth = g.getFontMetrics().stringWidth(vsLabel);
            g.drawString(vsLabel, (panelWidth - vsWidth) / 2, (panelHeight / 2) + 10);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            String p2Label = "COMPUTER";
            int p2LabelX = p2X + (cardW / 2) - (g.getFontMetrics().stringWidth(p2Label) / 2);
            g.drawString(p2Label, p2LabelX, startY - 20);

            if (currentP2Card != null) {
                currentP2Card.draw(g, p2X, startY);
            } else {
                // DRAWING A 'BACK OF THE CARD' IF IT'S COMPUTER'S TURN BUT NOT REVEALED YET
                if (isTurnInProgress) {
                    g.setColor(new Color(139, 0, 0)); // Dark Red back
                    g.fillRect(p2X, startY, cardW, cardH);
                    g.setColor(Color.WHITE);
                    g.drawRect(p2X, startY, cardW, cardH);
                    g.drawString("?", p2X + 45, startY + 75);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawRect(p2X, startY, cardW, cardH);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardGameApp().setVisible(true));
    }
}