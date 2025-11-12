package Tic_toc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.Timer;

// --- NEW CLASS: Custom JPanel for gradient background ---
class GradientPanel extends JPanel {
    private Color color1;
    private Color color2;

    public GradientPanel(Color c1, Color c2) {
        this.color1 = c1;
        this.color2 = c2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        // Create a gradient paint from top-left to bottom-right
        GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}
// --- END NEW CLASS ---

public class TicTacToeGame implements ActionListener {

    // --- 1. Declare necessary variables ---

    private JFrame frame;
    private JPanel titlePanel;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton[] buttons = new JButton[9];
    private boolean isPlayerXTurn = true;
    private int turnCount = 0;
    private Random random = new Random();

    // --- 2. Constructor (This runs when the game object is created) ---
    public TicTacToeGame() {

        // --- Set up the main window (Frame) ---
        frame = new JFrame("Tic-Tac-Toe (Player vs. AI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        // --- NEW --- Create our custom GradientPanel for the background
        GradientPanel backgroundPanel = new GradientPanel(new Color(173, 216, 230), new Color(255, 255, 204)); // Light Blue to Light Yellow
        backgroundPanel.setLayout(new BorderLayout()); // Set its layout to BorderLayout

        // --- Set up the Status Label (Text at the top) ---
        statusLabel = new JLabel();
        statusLabel.setText("Player X's turn");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 30));
        statusLabel.setOpaque(false); // --- MODIFIED --- Make it NOT opaque so gradient shows through

        titlePanel = new JPanel();
        titlePanel.add(statusLabel);
        titlePanel.setOpaque(false); // --- MODIFIED --- Make titlePanel NOT opaque
        
        // --- Set up the Board Panel (for the 9 buttons) ---
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5)); // --- MODIFIED --- Added gaps between buttons (5 pixels)
        boardPanel.setOpaque(false); // --- MODIFIED --- Make boardPanel NOT opaque

        // --- Create 9 buttons and add them to the board (using a for loop) ---
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
            buttons[i].setBackground(new Color(240, 240, 240)); // --- NEW --- Light gray for buttons
            boardPanel.add(buttons[i]);
        }
        
        // --- Add the panels to the BACKGROUND panel instead of directly to the frame ---
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);
        backgroundPanel.add(boardPanel, BorderLayout.CENTER);

        // --- Add the background panel to the frame ---
        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    // --- 3. Main Method (The starting point of the program) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TicTacToeGame();
            }
        });
    }

    // --- 4. ActionListener Method (This runs EVERY time any button is clicked) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                if (buttons[i].getText().equals("") && isPlayerXTurn) {
                    buttons[i].setText("X");
                    buttons[i].setForeground(Color.RED);
                    statusLabel.setText("Computer's (O) turn");
                    isPlayerXTurn = false;
                    turnCount++;
                    checkWin();

                    if (turnCount < 9 && buttons[0].isEnabled()) {
                        Timer timer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                computerMove();
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }
        }
    }

    // --- NEW METHOD --- For the computer's random move
    public void computerMove() {
        int computerGuess;

        // --- MODIFIED --- Check if there are any empty buttons left before trying to move
        boolean hasEmptyButton = false;
        for(JButton button : buttons) {
            if (button.getText().equals("")) {
                hasEmptyButton = true;
                break;
            }
        }

        if (!hasEmptyButton) { // If no empty buttons, it's a draw, so don't try to move
            draw();
            return;
        }

        while (true) {
            computerGuess = random.nextInt(9);
            if (buttons[computerGuess].getText().equals("")) {
                buttons[computerGuess].setText("O");
                buttons[computerGuess].setForeground(Color.BLUE);
                isPlayerXTurn = true;
                statusLabel.setText("Player X's turn");
                turnCount++;
                checkWin();
                break;
            }
        }
    }

    // --- 5. Method to Check for a Winner ---
    public void checkWin() {
        if (checkLine(0, 1, 2, "X")) xWins(0, 1, 2);
        else if (checkLine(3, 4, 5, "X")) xWins(3, 4, 5);
        else if (checkLine(6, 7, 8, "X")) xWins(6, 7, 8);
        else if (checkLine(0, 3, 6, "X")) xWins(0, 3, 6);
        else if (checkLine(1, 4, 7, "X")) xWins(1, 4, 7);
        else if (checkLine(2, 5, 8, "X")) xWins(2, 5, 8);
        else if (checkLine(0, 4, 8, "X")) xWins(0, 4, 8);
        else if (checkLine(2, 4, 6, "X")) xWins(2, 4, 6);

        else if (checkLine(0, 1, 2, "O")) oWins(0, 1, 2);
        else if (checkLine(3, 4, 5, "O")) oWins(3, 4, 5);
        else if (checkLine(6, 7, 8, "O")) oWins(6, 7, 8);
        else if (checkLine(0, 3, 6, "O")) oWins(0, 3, 6);
        else if (checkLine(1, 4, 7, "O")) oWins(1, 4, 7);
        else if (checkLine(2, 5, 8, "O")) oWins(2, 5, 8);
        else if (checkLine(0, 4, 8, "O")) oWins(0, 4, 8);
        else if (checkLine(2, 4, 6, "O")) oWins(2, 4, 6);

        else if (turnCount == 9) {
            draw();
        }
    }

    // --- 6. Helper Methods ---
    public boolean checkLine(int a, int b, int c, String player) {
        return buttons[a].getText().equals(player) &&
               buttons[b].getText().equals(player) &&
               buttons[c].getText().equals(player);
    }

    public void xWins(int a, int b, int c) {
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);
        disableAllButtons();
        statusLabel.setText("Player X Wins!");
        JOptionPane.showMessageDialog(frame, "Player X Wins!");
    }

    public void oWins(int a, int b, int c) {
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);
        disableAllButtons();
        statusLabel.setText("Computer (O) Wins!");
        JOptionPane.showMessageDialog(frame, "Computer (O) Wins!");
    }

    public void draw() {
        disableAllButtons();
        statusLabel.setText("It's a Draw!");
        JOptionPane.showMessageDialog(frame, "It's a Draw!");
    }

    public void disableAllButtons() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        }
    }
}