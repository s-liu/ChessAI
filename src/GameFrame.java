import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame {

	private GamePanel myPanel;
	private JMenuBar menuBar;
	private JMenu gameMenu;
	private JMenu settingsMenu;
	private JMenu helpMenu;
	private JMenuItem newGameOption;
	private JMenuItem undoOption;
	private JMenuItem quitOption;
	private JMenuItem selectDifficultyOption;
	private JMenuItem aboutOption;
	private JCheckBoxMenuItem blackIsAiOption;
	private JCheckBoxMenuItem whiteIsAiOption;
	private JCheckBoxMenuItem reverseBoardOption;
	private JTextArea textArea;
	private JScrollPane scrollPaneOne;
	private JScrollPane scrollPaneTwo;
	private JLabel moveLabel;
	private JLabel boardLabel;
	private JList list;
	private boolean screenLocked;
	private boolean blackIsAI;
	private boolean whiteIsAI;
	private ImageIcon chessBoardLabelImage;
	private ImageIcon reverseBoardLabelImage;

	public GameFrame() {

		// JFrame
		this.setTitle("Chess");
		this.setSize(520, 470);
		getContentPane().setLayout(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("image\\icon.png"));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		// Initialize variables
		whiteIsAI = false;
		blackIsAI = false;
		screenLocked = false;

		iniComponents();

	}

	private void iniComponents() {

		addMenu();
		// game panel
		myPanel = new GamePanel();
		getContentPane().add(myPanel);
		myPanel.setBounds(10, 10, 320, 320);

		// history JList
		list = new JList(myPanel.chessBoard.history);
		scrollPaneOne = new JScrollPane(list);
		scrollPaneOne.setBounds(345, 17, 167, 400);
		getContentPane().add(scrollPaneOne);

		// Text area which shows status of the game
		textArea = new JTextArea();
		scrollPaneTwo = new JScrollPane(textArea);
		scrollPaneTwo.setBounds(2, 347, 340, 70);
		textArea.setEditable(false);
		textArea.append("Welcome to chess game.\n");
		textArea.append("White's Turn.\n");
		getContentPane().add(scrollPaneTwo);

		// the label at the top of the history
		moveLabel = new JLabel("Moves", SwingConstants.CENTER);
		moveLabel.setBounds(345, 3, 165, 10);
		getContentPane().add(moveLabel);

		// images for the chess board label (a-h, 1-8)
		chessBoardLabelImage = new ImageIcon("image\\chessboardlabel.jpg");
		reverseBoardLabelImage = new ImageIcon(
				"image\\reversechessboardlabel.jpg");

		// chess board label
		boardLabel = new JLabel(chessBoardLabelImage);
		boardLabel.setBounds(0, 0, 340, 340);
		getContentPane().add(boardLabel);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new GameFrame().setVisible(true);
			}
		});
	}

	/**
	 * Add menu to the current frame
	 */
	public void addMenu() {

		menuBar = new JMenuBar();

		// menus
		gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');
		settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic('S');
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		// menu items
		newGameOption = new JMenuItem("New Game");
		newGameOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		quitOption = new JMenuItem("Quit");
		quitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		undoOption = new JMenuItem("Undo");
		undoOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK));

		blackIsAiOption = new JCheckBoxMenuItem("Black is AI");
		whiteIsAiOption = new JCheckBoxMenuItem("White is AI");
		reverseBoardOption = new JCheckBoxMenuItem("Reverse Board");
		selectDifficultyOption = new JMenuItem("Select AI Difficulty");
		aboutOption = new JMenuItem("About");

		// add listeners to menu items
		newGameOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the New Game menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent e) {
				myPanel.chessBoard.newGame();
				myPanel.repaint();
				screenLocked = false;
				textArea.setText("");
				textArea.append("Welcome to chess game.\n");
				textArea.append("White's Turn.\n");
				if (whiteIsAI) {
					myPanel.aiMove(true);
				}
			}
		}

		);

		undoOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the Undo menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent e) {
				myPanel.chessBoard.undoMove();
				if (myPanel.chessBoard.currentPlayer == true) {
					textArea.append("White's Turn.\n");
				} else {
					textArea.append("Black's Turn.\n");
				}
				myPanel.repaint();
			}

		}

		);

		quitOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the Quit menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}

		);

		whiteIsAiOption.setSelected(false);
		whiteIsAiOption.addActionListener(new java.awt.event.ActionListener() {
			/**
			 * Responds to the White is AI menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				whiteIsAI = whiteIsAiOption.isSelected();
				if (blackIsAiOption.isSelected()) {
					blackIsAiOption.setSelected(false);
					blackIsAI = false;
				}
			}
		});

		blackIsAiOption.setSelected(false);
		blackIsAiOption.addActionListener(new java.awt.event.ActionListener() {
			/**
			 * Responds to the Black is AI menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				blackIsAI = blackIsAiOption.isSelected();
				if (whiteIsAiOption.isSelected()) {
					whiteIsAiOption.setSelected(false);
					whiteIsAI = false;
				}
			}
		});

		reverseBoardOption.setSelected(false);
		reverseBoardOption
				.addActionListener(new java.awt.event.ActionListener() {
					/**
					 * Responds to the Reverse board menu option
					 * 
					 * @param event
					 *            The event that selected this menu option
					 */
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						if (reverseBoardOption.isSelected()) {
							boardLabel.setIcon(reverseBoardLabelImage);
						} else {
							boardLabel.setIcon(chessBoardLabelImage);
						}

						myPanel.boardIsReversed = reverseBoardOption
								.isSelected();
						myPanel.repaint();
					}
				}

				);

		selectDifficultyOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the Undo menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent e) {
				int choice = selectAIDifficulty() + 1;
				if (choice > 0) {
					AI.updateNoOfPlies(choice);
				}
			}

		}

		);

		aboutOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the About menu option
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Chess - A Chess Game\nCopyright (C) 2012 Chen L, David X, Nash B\n<ics4ue2011@gmail.com>",
								"About", JOptionPane.INFORMATION_MESSAGE);

			}

		}

		);

		// add all items to the menu
		gameMenu.add(newGameOption);
		gameMenu.add(undoOption);
		gameMenu.addSeparator();
		gameMenu.add(quitOption);
		settingsMenu.add(blackIsAiOption);
		settingsMenu.add(whiteIsAiOption);
		settingsMenu.addSeparator();
		settingsMenu.add(reverseBoardOption);
		settingsMenu.addSeparator();
		settingsMenu.add(selectDifficultyOption);
		helpMenu.add(aboutOption);
		menuBar.add(gameMenu);
		menuBar.add(settingsMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	}

	public int selectAIDifficulty() {
		Object[] option = new Object[5];
		// Displays the pictures of piece choices.

		option[0] = new ImageIcon("image\\pawnw.gif");
		option[1] = new ImageIcon("image\\bishopw.gif");
		option[2] = new ImageIcon("image\\knightw.gif");
		option[3] = new ImageIcon("image\\rookw.gif");
		option[4] = new ImageIcon("image\\queenw.gif");

		int choice = JOptionPane.showOptionDialog(null,
				"The current level of difficulty is : " + AI.getNoOfPlies()
						+ "\n" + "Please select the level of difficulty: ",
				"Select AI Difficulty", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
		return choice;
	}

	public class GamePanel extends JPanel {

		// size of the board in number of squares
		final int BOARD_SIZE = 8;
		// size of each square on the board in pixels
		final int SQUARE_SIZE = 40;
		private final boolean BLACK = false;
		private final boolean WHITE = true;
		// used to keep track of the piece selected
		private Point currentCoordinate;
		private Set<Point> currentPossibleMoves;
		private Piece selectedPiece;
		private int draggedXPos, draggedYPos;
		// Board object
		private Board chessBoard;
		// reverse board
		private boolean boardIsReversed;

		// Load background images
		Image imageBackground = new ImageIcon("image\\chessboard.jpg")
				.getImage();
		Image highlightRegion = new ImageIcon("image\\highlight.jpg")
				.getImage();
		Image highlightEnemy = new ImageIcon("image\\highlightenemy.jpg")
				.getImage();
		Image reverseBackground = new ImageIcon("image\\reversechessboard.jpg")
				.getImage();

		public GamePanel() {

			// initialize variables
			setLayout(null);
			chessBoard = new Board(BOARD_SIZE);
			boardIsReversed = false;

			Piece.updateBoard(chessBoard);

			this.addMouseListener(new MouseHandler());
			this.addMouseMotionListener(new MouseMotionHandler());
			this.setFocusable(true);
			this.requestFocusInWindow();
			repaint();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			// draw reversed board
			if (boardIsReversed) {
				g.drawImage(reverseBackground, 0, 0, this);

				// reversed highlight regions
				if (currentPossibleMoves != null) {
					for (Point next : currentPossibleMoves) {
						if (chessBoard.spaceIsEmpty(next.x, next.y)) {
							g.drawImage(highlightRegion,
									(BOARD_SIZE - 1 - next.x) * SQUARE_SIZE,
									next.y * SQUARE_SIZE, null);
						} else {
							g.drawImage(highlightEnemy,
									(BOARD_SIZE - 1 - next.x) * SQUARE_SIZE,
									next.y * SQUARE_SIZE, null);
						}
					}
				}

				// reversed pieces
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						if (chessBoard.getPiece(i, j) != null) {
							chessBoard.getPiece(i, j).draw(g,
									(BOARD_SIZE - 1 - i) * SQUARE_SIZE,
									j * SQUARE_SIZE);
						}
					}
				}

				// reversed current piece
				if (selectedPiece != null) {
					selectedPiece.draw(g, draggedXPos, draggedYPos);

				}
			} else {

				// Draw the background

				g.drawImage(imageBackground, 0, 0, this);

				// Highlight the regions for valid moves
				if (currentPossibleMoves != null) {
					for (Point next : currentPossibleMoves) {
						if (chessBoard.spaceIsEmpty(next.x, next.y)) {
							g.drawImage(highlightRegion, next.x * SQUARE_SIZE,
									(BOARD_SIZE - 1 - next.y) * SQUARE_SIZE,
									null);
						} else {
							g.drawImage(highlightEnemy, next.x * SQUARE_SIZE,
									(BOARD_SIZE - 1 - next.y) * SQUARE_SIZE,
									null);
						}
					}
				}

				// Draw the pieces
				for (int i = 0; i < BOARD_SIZE; i++) {
					for (int j = 0; j < BOARD_SIZE; j++) {
						if (chessBoard.getPiece(i, j) != null) {
							chessBoard.getPiece(i, j).draw(g, i * SQUARE_SIZE,
									(BOARD_SIZE - 1 - j) * SQUARE_SIZE);
						}
					}
				}

				// Draw the current piece
				if (selectedPiece != null) {
					selectedPiece.draw(g, draggedXPos, draggedYPos);

				}
			}

		}


		/**
		 * Display whether is the white's turn or black's turn on the text area
		 */
		public void displayTurn() {
			if (chessBoard.currentPlayer == WHITE) {

				textArea.append("White's Turn.\n");
			} else {

				textArea.append("Black's Turn.\n");
			}
		}

		/**
		 * Display in-check messages
		 */
		public void displayInCheck() {

			displayTurn();
			if (chessBoard.currentPlayer == WHITE) {

				if (chessBoard.whiteIsInCheck) {
					textArea.append("White is in Check.\n");
				}
				if (chessBoard.isCheckmate(WHITE)) {
					JOptionPane.showMessageDialog(null, "Checkmate on White","Checkmate",
                            JOptionPane.INFORMATION_MESSAGE);
					textArea.append("Checkmate on White. Black won.\n");
					textArea.append("Game over. Please start a new game.\n");
					screenLocked = true;
				}
				if (chessBoard.isStalemate(WHITE)) {
					JOptionPane.showMessageDialog(null, "Stalemate","Stalemate",
                            JOptionPane.INFORMATION_MESSAGE);
					textArea.append("Stalemate. Round draw.\n");
					textArea.append("Game over. Please start a new game.\n");
					screenLocked = true;
				}
			} else {

				if (chessBoard.blackIsInCheck) {
					textArea.append("Black is in Check.\n");
				}
				if (chessBoard.isCheckmate(BLACK)) {
					JOptionPane.showMessageDialog(null, "Checkmate on Black","Checkmate",
                            JOptionPane.INFORMATION_MESSAGE);
					textArea.append("Checkmate on Black. White won.\n");
					textArea.append("Game over. Please start a new game.\n");
					screenLocked = true;
				}
				if (chessBoard.isStalemate(BLACK)) {
					JOptionPane.showMessageDialog(null, "Stalemate","Stalemate",
                            JOptionPane.INFORMATION_MESSAGE);
					textArea.append("Stalemate. Round draw.\n");
					textArea.append("Game over. Please start a new game.\n");
					screenLocked = true;
				}

			}
		}

		private class MouseHandler extends MouseAdapter {

			private int fromX, fromY;

			/**
			 * Finds the piece clicked on.
			 * 
			 * @param event
			 *            information about the mouse pressed event
			 */
			public void mousePressed(MouseEvent event) {

				// Get clicks only if the game is still in play
				if (!screenLocked) {

					Point pressedPoint = event.getPoint();
					// convert x y positions to rows and columns
					if (boardIsReversed) {
						fromX = BOARD_SIZE - 1 - pressedPoint.x / SQUARE_SIZE;

						fromY = pressedPoint.y / SQUARE_SIZE;
					} else {
						fromX = pressedPoint.x / SQUARE_SIZE;

						fromY = BOARD_SIZE - 1 - pressedPoint.y / SQUARE_SIZE;
					}

					// Check if the selected square has a player piece
					if (chessBoard.getPiece(fromX, fromY) != null
							&& chessBoard.getPiece(fromX, fromY).color == chessBoard.currentPlayer) {
						// get coordinate, set of all possible moves of the
						// selected piece
						selectedPiece = chessBoard.getPiece(fromX, fromY);
						currentCoordinate = new Point(fromX, fromY);
						currentPossibleMoves = selectedPiece
								.generateValidMoves();
						// set original position of the selected piece to null
						// (piece is lifted)
						chessBoard.setBoard(currentCoordinate, null);
					}

				}
			}

			/**
			 * Finds where the mouse was released and moves the piece, if
			 * allowed
			 * 
			 * @param event
			 *            information about the mouse released event
			 */
			public void mouseReleased(MouseEvent event) {

				// Get clicks only if the game is still in play
				if (!screenLocked) {

					Point releasedPoint = event.getPoint();
					int toX, toY;

					// convert the release point to row and column
					if (boardIsReversed) {
						toX = BOARD_SIZE
								- 1
								- (int) ((double) releasedPoint.x / SQUARE_SIZE);

						toY = (int) ((double) releasedPoint.y / SQUARE_SIZE);
					} else {
						toX = (int) ((double) releasedPoint.x / SQUARE_SIZE);

						toY = BOARD_SIZE
								- 1
								- (int) ((double) releasedPoint.y / SQUARE_SIZE);

					}

					// Move piece, if a piece is selected and the move is valid
					if (selectedPiece != null
							&& toX >= 0
							&& toX < BOARD_SIZE
							&& toY >= 0
							&& toY < BOARD_SIZE
							&& currentPossibleMoves
									.contains(new Point(toX, toY))) {
						// make the move on the board
						// player is switched
						chessBoard.moveTo(selectedPiece, toX, toY);
						// set cursor back
						setCursor(Cursor.getDefaultCursor());

						// determine whether the opponent is in check
						displayInCheck();

						// if AI is turned on, ai's turn
						if (blackIsAI && chessBoard.currentPlayer == BLACK
								&& !screenLocked) {

							aiMove(BLACK);

						} else if (whiteIsAI
								&& chessBoard.currentPlayer == WHITE
								&& !screenLocked) {

							aiMove(WHITE);

						}

					} else if (selectedPiece != null) {
						// Set the chess piece back if the move is invalid

						chessBoard.setBoard(currentCoordinate, selectedPiece);
					}

					// clear all data of the piece moved
					selectedPiece = null;
					currentCoordinate = null;
					currentPossibleMoves = null;
					Piece.updateBoard(chessBoard);
					setCursor(Cursor.getDefaultCursor());

					repaint();

				}
			}
		}

		private class MouseMotionHandler extends MouseMotionAdapter {
			/**
			 * Changes the mouse cursor to a hand if it is over top of a piece
			 * 
			 * @param event
			 *            information about the mouse released event
			 */
			public void mouseMoved(MouseEvent event) {

				int x, y;
				// Locate the board row and column of the mouse
				if (boardIsReversed) {
					x = BOARD_SIZE - 1 - (event.getX() / SQUARE_SIZE);
					y = event.getY() / SQUARE_SIZE;
				} else {
					x = event.getX() / SQUARE_SIZE;
					y = 7 - (event.getY() / SQUARE_SIZE);
				}

				if (chessBoard.getPiece(x, y) != null
						&& chessBoard.getPiece(x, y).color == chessBoard.currentPlayer)
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else
					setCursor(Cursor.getDefaultCursor());
			}

			/**
			 * Moves the selected piece when the mouse is dragged
			 * 
			 * @param event
			 *            information about the mouse released event
			 */
			public void mouseDragged(MouseEvent event) {
				draggedXPos = event.getX() - SQUARE_SIZE / 2;
				draggedYPos = event.getY() - SQUARE_SIZE / 2;
				repaint();
			}

		}

		public class AIThread implements Runnable {

			boolean AIColor;

			/**
			 * Create a new thread with parameter (color of the AI) for AI
			 * 
			 * @param AIColor
			 *            color of AI
			 */
			public AIThread(boolean AIColor) {
				// store parameter for later use
				this.AIColor = AIColor;
			}

			public void run() {
				screenLocked = true;
				repaint();
				try {

					Thread.sleep(100);

				}

				catch (Exception ex) {

				}
				AI ai = new AI(chessBoard, AIColor);
				Move aiMove = ai.getBestMove();
				Piece.updateBoard(chessBoard);
				chessBoard.makeMove(aiMove);
				repaint();
				displayInCheck();
				screenLocked = false;
			}
		}

		/**
		 * AI's turn to make a move
		 * 
		 * @param AIColor
		 *            color of the AI
		 */
		public void aiMove(boolean AIColor) {
			Runnable r = new AIThread(AIColor);
			textArea.append("AI running...\n");
			// create a separate thread for ai's move
			new Thread(r).start();
		}
	}

}
