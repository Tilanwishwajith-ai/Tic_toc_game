package Tic_toc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer; // Required for the computer's move delay

// --- Custom JPanel class for drawing a gradient background ---
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
        // Create a gradient paint from top-left (0,0) to bottom-right (w,h)
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
    private boolean isPlayerXTurn = true; // Player X (Human) goes first
    private int turnCount = 0;
    // We no longer need 'private Random random = new Random();' for the hard AI

    // --- 2. Constructor (This runs when the game object is created) ---
    public TicTacToeGame() {

        // --- Set up the main window (Frame) ---
        frame = new JFrame("X / O Game (Unbeatable AI)"); // Title updated
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null); // Open window in the center

        // --- Set up the Gradient Background ---
        GradientPanel backgroundPanel = new GradientPanel(new Color(255, 255, 224), Color.WHITE); // Light Yellow to White
        backgroundPanel.setLayout(new BorderLayout());

        // --- Set up the Status Label (Text at the top) ---
        statusLabel = new JLabel();
        statusLabel.setText("Player X's turn");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 30));
        statusLabel.setOpaque(false); // Make transparent to see gradient

        titlePanel = new JPanel();
        titlePanel.add(statusLabel);
        titlePanel.setOpaque(false); // Make titlePanel transparent
        
        // --- Set up the Board Panel (for the 9 buttons) ---
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5)); // 5px gap between buttons
        boardPanel.setOpaque(false); // Make boardPanel transparent

        // --- Create 9 buttons and add them to the board (using a for loop) ---
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
            buttons[i].setBackground(new Color(240, 240, 240)); // Light gray for buttons
            boardPanel.add(buttons[i]);
        }
        
        // --- Add the panels to the BACKGROUND panel ---
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);
        backgroundPanel.add(boardPanel, BorderLayout.CENTER);

        // --- Add the single background panel to the frame ---
        frame.add(backgroundPanel);
        frame.setVisible(true); // Make the window visible
    }

    // --- 3. Main Method (The starting point of the program) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TicTacToeGame();
            }
        });
    }

    // --- 4. ActionListener Method (Runs when any button is clicked) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // Find out WHICH button was clicked
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                
                // Only act if the button is empty AND it's the Player's (X) turn
                if (buttons[i].getText().equals("") && isPlayerXTurn) {
                    
                    // Player X's move
                    buttons[i].setText("X");
                    buttons[i].setForeground(Color.RED);
                    statusLabel.setText("Computer's (O) turn");
                    isPlayerXTurn = false; // It's now the computer's turn
                    turnCount++;
                    
                    // Check if Player X won or it's a draw
                    // We check *before* the computer moves
                    checkWin(); 

                    // Check if the game is not over
                    if (turnCount < 9 && buttons[0].isEnabled()) {
                        
                        // Use a Timer to create a short delay for the computer's move
                        Timer timer = new Timer(500, new ActionListener() { // 500ms delay
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                computerMove(); // Call the computer's move logic
                            }
                        });
                        timer.setRepeats(false); // Only run once
                        timer.start();
                    }
                }
            }
        }
    }

    // --- 5. MODIFIED METHOD --- This now calls the Minimax AI ---
    public void computerMove() {
        // Find the best possible move
        int bestMove = findBestMove();

        // Make the move
        if (bestMove != -1) { // -1 would mean an error
            buttons[bestMove].setText("O");
            buttons[bestMove].setForeground(Color.BLUE);
            isPlayerXTurn = true; // Give the turn back to Player X
            statusLabel.setText("Player X's turn");
            turnCount++;
            
            // Check if the computer ("O") won with this move
            checkWin(); 
        }
    }


    // --- 6. MINIMAX AI LOGIC (NEW METHODS) ---

    /**
     * This is the "driver" for the minimax algorithm.
     * It loops through all empty spots and finds the one with the highest score.
     */
    private int findBestMove() {
        int bestScore = -1000; // Start with a very low score
        int bestMove = -1;     // -1 means no move found yet

        // Loop through all 9 board positions
        for (int i = 0; i < 9; i++) {
            // Check if the spot is empty
            if (buttons[i].getText().equals("")) {
                
                // --- Make the test move ---
                buttons[i].setText("O"); 
                
                // Call minimax for the *opponent* (minimizing player)
                // The AI is 'O' (maximizer), so the next turn is 'X' (minimizer)
                int score = minimax(false); // 'false' means it's the minimizer's turn
                
                // --- Undo the test move ---
                buttons[i].setText(""); 

                // If this move's score is better than the current best score
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    /**
     * The core recursive Minimax algorithm.
     * @param isMaximizingTurn True if it's the Computer's (O) turn, False if it's the Player's (X) turn.
     * @return The best score (10, 0, or -10) from this board state.
     */
    private int minimax(boolean isMaximizingTurn) {
        
        // --- Base Cases: Check if the game has ended ---
        int score = evaluateBoard();

        // If Computer (O) has won, return score
        if (score == 10) return score;
        // If Player (X) has won, return score
        if (score == -10) return score;
        // If it's a draw (no empty spots left), return 0
        if (turnCount + (isMaximizingTurn ? 1 : 0) == 9 && score == 0) {
             // A simplified check for a draw (this isn't perfect, but works for our logic)
             // A better check is 'isBoardFull()'
             if (isBoardFull()) return 0;
        }

        // --- Recursive Part ---

        if (isMaximizingTurn) { // Computer's (O) turn
            int best = -1000; // Start with a very low score
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("O"); // Make the move
                    best = Math.max(best, minimax(false)); // Recurse, now it's minimizer's turn
                    buttons[i].setText(""); // Undo the move
                }
            }
            return best;
        } 
        else { // Player's (X) turn
            int best = 1000; // Start with a very high score
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("X"); // Make the move
                    best = Math.min(best, minimax(true)); // Recurse, now it's maximizer's turn
                    buttons[i].setText(""); // Undo the move
                }
            }
            return best;
        }
    }

    /**
     * Checks the current board state and returns a score.
     * Does NOT modify the GUI.
     * @return 10 if O wins, -10 if X wins, 0 otherwise.
     */
    private int evaluateBoard() {
        // Check for 'O' (Computer) wins
        if (checkLine(0, 1, 2, "O") || checkLine(3, 4, 5, "O") || checkLine(6, 7, 8, "O") || // Rows
            checkLine(0, 3, 6, "O") || checkLine(1, 4, 7, "O") || checkLine(2, 5, 8, "O") || // Columns
            checkLine(0, 4, 8, "O") || checkLine(2, 4, 6, "O")) { // Diagonals
            return 10;
        }
        
        // Check for 'X' (Player) wins
        if (checkLine(0, 1, 2, "X") || checkLine(3, 4, 5, "X") || checkLine(6, 7, 8, "X") || // Rows
            checkLine(0, 3, 6, "X") || checkLine(1, 4, 7, "X") || checkLine(2, 5, 8, "X") || // Columns
            checkLine(0, 4, 8, "X") || checkLine(2, 4, 6, "X")) { // Diagonals
            return -10;
        }

        // If no one has won
        return 0;
    }

    /**
     * Helper to check if the board is full.
     * @return true if no empty spots, false otherwise.
     */
    private boolean isBoardFull() {
        for(int i = 0; i < 9; i++) {
            if(buttons[i].getText().equals("")) {
                return false; // Found an empty spot
            }
        }
        return true; // No empty spots
    }

    
    // --- 7. Method to Check for a Winner (GUI logic) ---
    // This is different from evaluateBoard(). This one *ends* the game.
    public void checkWin() {
        // --- Check X ---
        if (checkLine(0, 1, 2, "X")) xWins(0, 1, 2);
        else if (checkLine(3, 4, 5, "X")) xWins(3, 4, 5);
        else if (checkLine(6, 7, 8, "X")) xWins(6, 7, 8);
        else if (checkLine(0, 3, 6, "X")) xWins(0, 3, 6);
        else if (checkLine(1, 4, 7, "X")) xWins(1, 4, 7);
        else if (checkLine(2, 5, 8, "X")) xWins(2, 5, 8);
        else if (checkLine(0, 4, 8, "X")) xWins(0, 4, 8);
        else if (checkLine(2, 4, 6, "X")) xWins(2, 4, 6);

        // --- Check O ---
        else if (checkLine(0, 1, 2, "O")) oWins(0, 1, 2);
        else if (checkLine(3, 4, 5, "O")) oWins(3, 4, 5);
        else if (checkLine(6, 7, 8, "O")) oWins(6, 7, 8);
        else if (checkLine(0, 3, 6, "O")) oWins(0, 3, 6);
        else if (checkLine(1, 4, 7, "O")) oWins(1, 4, 7);
        else if (checkLine(2, 5, 8, "O")) oWins(2, 5, 8);
        else if (checkLine(0, 4, 8, "O")) oWins(0, 4, 8);
        else if (checkLine(2, 4, 6, "O")) oWins(2, 4, 6);

        // --- Check Draw ---
        // If 9 turns have passed and no one has won
        else if (turnCount == 9) {
            draw();
        }
    }

    // --- 8. Helper Methods (for GUI and game state) ---
    
    // Helper method to check if 3 buttons match a player
    public boolean checkLine(int a, int b, int c, String player) {
        return buttons[a].getText().equals(player) &&
               buttons[b].getText().equals(player) &&
               buttons[c].getText().equals(player);
    }

    // Runs when X wins
    public void xWins(int a, int b, int c) {
        buttons[a].setBackground(Color.GREEN); // Highlight winning line
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);
        disableAllButtons();
        statusLabel.setText("Player X Wins!");
        JOptionPane.showMessageDialog(frame, "Player X Wins!");
    }

    // Runs when O (Computer) wins
    public void oWins(int a, int b, int c) {
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);
        disableAllButtons();
        statusLabel.setText("Computer (O) Wins!");
        JOptionPane.showMessageDialog(frame, "Computer (O) Wins!");
    }

    // Runs when the game is a draw
    public void draw() {
        disableAllButtons();
        statusLabel.setText("It's a Draw!");
        JOptionPane.showMessageDialog(frame, "It's a Draw!");
    }

    // Disables all buttons after the game ends
    public void disableAllButtons() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        }
    }
}