package edu.nyu.pqs.connectfour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The {@code ConnectFourView} provides a view for the connect
 * four game to be used with {@code ConnectFourModel}.
 * The class implements the {@code ConnectFourObserver} interface 
 * and its methods, and holds a reference to a {@code ConnectFourSubject}.
 * 
 * @author Eric
 * @see ConnectFourObserver
 * @see ConnectFourSubject
 */
public class ConnectFourView implements ConnectFourObserver {
  private ConnectFourSubject connectFour;
  private JFrame frame = new JFrame("Connect Four");
  private JPanel mainPanel = new JPanel();
  private JPanel gridPanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private static final int NUM_COLUMNS = 7;
  private static final int NUM_ROWS = 6;
  static JPanel[][] gridArray = new JPanel[NUM_ROWS][NUM_COLUMNS];
  Color[][] gameGrid = new Color[NUM_ROWS][NUM_COLUMNS];
  private JButton[] columnArrows = new JButton[NUM_COLUMNS];

  public ConnectFourView() {
    mainMenu();
  }

  /**
   * Provides an initial menu for the user to choose between
   * one and two player games.
   * When clicked the method calls a listener that creates
   * a {@code ConnectFourModel} object with either two players
   * or one player and one computer player.
   */
  private void mainMenu() {
    JFrame menuFrame = new JFrame("Main Menu");
    JPanel menuPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    menuPanel.setLayout(new BorderLayout());
    buttonPanel.setLayout(new GridLayout(1, 2));
    JButton onePlayer = new JButton("1 Player");
    JButton twoPlayers = new JButton("2 Players");
    onePlayer.setSize(100, 100);
    twoPlayers.setSize(100, 100);
    onePlayer.addActionListener(new mainMenuListener(1, menuFrame));
    twoPlayers.addActionListener(new mainMenuListener(2, menuFrame));
    buttonPanel.add(onePlayer);
    buttonPanel.add(twoPlayers);
    menuPanel.add(buttonPanel, BorderLayout.CENTER);
    menuFrame.setContentPane(menuPanel);
    menuFrame.setSize(400, 400);
    menuFrame.setLocationRelativeTo(null);
    menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    menuFrame.setVisible(true);
    menuFrame.setResizable(false);
  }

  /**
   * Provides a view of the connect four board. This method
   * is called after a user chooses one or two player game.
   */
  private void display() {
    initgameGrid();
    mainPanel.setLayout(new BorderLayout());
    gridPanel.setLayout(new GridLayout(NUM_ROWS, NUM_COLUMNS));
    buttonPanel.setLayout(new GridLayout(1, NUM_COLUMNS));
    buttonPanel.setSize(730, 100);
    mainPanel.setBackground(new Color(23, 13, 44));
    fillGrid();
    mainPanel.add(gridPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.NORTH);
    createButtonListeners();
    frame.setContentPane(mainPanel);
    frame.setSize(846, 730);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setResizable(false);
  }

  /**
   * Initializes the connect four board to all white
   * pieces.
   */
  private void initgameGrid() {
    for (int i = 0; i < NUM_ROWS; i++) {
      for (int j = 0; j < NUM_COLUMNS; j++) {
        gameGrid[i][j] = Color.WHITE;
      }
    }
  }

  /**
   * Creates {@code PiecePanel} objects for each position
   * of the connect four board and adds them to the view.
   * @see PiecePanel
   */
  private void fillGrid() {
    for (int row = 0; row < NUM_ROWS; row++) {
      for (int col = 0; col < NUM_COLUMNS; col++) {
        gridArray[row][col] = new PiecePanel(row, col);
        gridPanel.add(gridArray[row][col]);
      }
    }
  }

  /**
   * Adds {@code JButtons} above each column of the board
   * and adds listeners to each of them to update the board
   * when the button is clicked.
   */
  private void createButtonListeners() {
    for (int i = 0; i < NUM_COLUMNS; i++) {
      columnArrows[i] = new JButton();
      columnArrows[i].addActionListener(new ButtonClickedListener());
      buttonPanel.add(columnArrows[i]);
    }
  }

  @Override
  public void updateBoardDisplay(int row, int col, Color color) {
    gameGrid[row][col] = color;
    gridArray[row][col].repaint();
  }

  @Override
  public void gameOver(ConnectFourColors winnerColor) {
    JFrame menuFrame = new JFrame("Game Over");
    JPanel menuPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    menuPanel.setLayout(new BorderLayout());
    buttonPanel.setLayout(new GridLayout(1, 1));
    String labelText = winnerColor + " wins";
    JLabel label = new JLabel(labelText);
    menuPanel.add(label, BorderLayout.CENTER);
    JButton exit = new JButton("Exit Game");
    exit.setSize(100, 100);
    exit.addActionListener(new gameOverListener(menuFrame));
    buttonPanel.add(exit);
    menuPanel.add(buttonPanel, BorderLayout.SOUTH);
    menuFrame.setContentPane(menuPanel);
    menuFrame.setSize(400, 400);
    menuFrame.setLocationRelativeTo(null);
    menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    menuFrame.setVisible(true);
    menuFrame.setResizable(false);
  }

  @Override
  public void boardFull() {
    JFrame menuFrame = new JFrame("Game Over");
    JPanel menuPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    menuPanel.setLayout(new BorderLayout());
    buttonPanel.setLayout(new GridLayout(1, 1));
    JLabel label = new JLabel("It's a tie");
    menuPanel.add(label, BorderLayout.CENTER);
    JButton exit = new JButton("Exit Game");
    exit.setSize(100, 100);
    exit.addActionListener(new gameOverListener(menuFrame));
    buttonPanel.add(exit);
    menuPanel.add(buttonPanel, BorderLayout.SOUTH);
    menuFrame.setContentPane(menuPanel);
    menuFrame.setSize(400, 400);
    menuFrame.setLocationRelativeTo(null);
    menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    menuFrame.setVisible(true);
    menuFrame.setResizable(false);
  }

  /**
   * The {@code PiecePanel} class extends {@code JPanel}
   * and implements each row/column position of the connect four
   * board. PiecePanel object's colors are updated when a player
   * fills that position on the board.
   * @author Eric
   * @see JPanel
   */
  @SuppressWarnings("serial")
  class PiecePanel extends JPanel {
    int row;
    int column;
    
    public PiecePanel(int r, int c) {
      row = r;
      column = c;
      this.setBackground(Color.YELLOW);
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Color c = gameGrid[row][column];
      g.setColor(c);
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.fillOval(10, 10, 90, 90);
      g2d.dispose();
    }
  }

  /**
   * The {@code ButtonClickedListener} implements the 
   * {@code ActionListener} interface to listen for when
   * a column button is clicked and update the view and model
   * accordingly.
   * @author Eric
   * @see ActionListener
   */
  private class ButtonClickedListener implements ActionListener {
  
    @Override
    public void actionPerformed(ActionEvent e) {
      Object column = e.getSource();
      for (int i = 0; i < NUM_COLUMNS; i++) {
        if (column == columnArrows[i]) {
          connectFour.playerMove(i);
        }
      }
    }
  }

  /**
   * The {@code mainMenuListener} implements the 
   * {@code ActionListener} interface to listen for whether
   * a user clicks on one or two player game button and creates
   * a {@code ConnectFourModel} accordingly.
   * @author Eric
   * @see ActionListener
   */
  private class mainMenuListener implements ActionListener {
    private int numPlayers = 0;
    private JFrame frame;

    private mainMenuListener(int numPlayers, JFrame frame) {
      this.numPlayers = numPlayers;
      this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (numPlayers == 2) {
        connectFour = new ConnectFourModel.Builder(ConnectFourHumanPlayer.
            getHumanPlayer(ConnectFourColors.RED)).
            secondPlayer(ConnectFourHumanPlayer.
            getHumanPlayer(ConnectFourColors.BLACK)).build();
      } else {
        connectFour = new ConnectFourModel.Builder(ConnectFourHumanPlayer.
            getHumanPlayer(ConnectFourColors.RED))
            .secondPlayer(ConnectFourComputerPlayer.
            getComputerPlayer()).build(); 
      }
      connectFour.registerObserver(ConnectFourView.this);
      display();
      frame.dispose();
    }
  }

  /**
   * The {@code gameOverListener} implements the 
   * {@code ActionListener} interface to listen for when
   * the connect four game ends.
   * @author Eric
   * @see ActionListener
   */
  private class gameOverListener implements ActionListener {
    private JFrame gameOverframe;

    private gameOverListener(JFrame gameOverframe) {
      this.gameOverframe = gameOverframe;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      frame.dispose();
      gameOverframe.dispose();
    }
  }
}