import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Board {

	public Piece[][] board;
	private int size;

	// used for board evaluation
	static int[] pieceOwn = new int[] { 100, 300, 300, 500, 1000, 9999 };
	static int[] pieceThreats = new int[] { 0, -4, -8, -12, -20, -16 };

	private final boolean BLACK = false;
	private final boolean WHITE = true;
	private final int PAWN = 0;
	private final int BISHOP = 1;
	private final int KNIGHT = 2;
	private final int ROOK = 3;
	private final int QUEEN = 4;
	private final int KING = 5;

	// the history of moves made
	protected DefaultListModel history;

	protected King whiteKing;
	protected King blackKing;

	protected boolean currentPlayer;

	public boolean blackIsInCheck;
	public boolean whiteIsInCheck;

	/**
	 * Creates a new board for the start of a game.
	 */

	public Board(int size) {
		this.size = size;
		board = new Piece[size][size];
		blackIsInCheck = false;
		whiteIsInCheck = false;
		history = new DefaultListModel();
		newGame();
		// Piece.updateBoard(this);
	}

	/**
	 * Creates a copy of a existing board.
	 */

	public Board(Board other, boolean player) {

		board = new Piece[8][8];
		blackIsInCheck = false;
		whiteIsInCheck = false;
		currentPlayer = player;
		history = new DefaultListModel();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (other.getPiece(i, j) != null) {
					Piece placement = other.getPiece(i, j).getCopyOf();
					board[i][j] = placement;
					if (placement.identity == KING) {

						if (placement.color == WHITE) {
							whiteKing = (King) placement;
						} else {
							blackKing = (King) placement;
						}

					}
				}
			}
		}

		updateThreats();
		
	}

	/**
	 * Sets the board with a standard, new game configuration.
	 */
	public void newGame() {

		currentPlayer = WHITE;
		board[0][0] = new Rook(WHITE, 0, 0);
		board[7][0] = new Rook(WHITE, 7, 0);
		board[1][0] = new Knight(WHITE, 1, 0);
		board[6][0] = new Knight(WHITE, 6, 0);
		board[2][0] = new Bishop(WHITE, 2, 0);
		board[5][0] = new Bishop(WHITE, 5, 0);
		board[3][0] = new Queen(WHITE, 3, 0);
		board[4][0] = new King(WHITE, 4, 0);
		whiteKing = (King) board[4][0];
		for (int i = 0; i < size; i++) {
			board[i][1] = new Pawn(WHITE, i, 1);
		}

		board[0][7] = new Rook(BLACK, 0, 7);
		board[7][7] = new Rook(BLACK, 7, 7);
		board[1][7] = new Knight(BLACK, 1, 7);
		board[6][7] = new Knight(BLACK, 6, 7);
		board[2][7] = new Bishop(BLACK, 2, 7);
		board[5][7] = new Bishop(BLACK, 5, 7);
		board[3][7] = new Queen(BLACK, 3, 7);
		board[4][7] = new King(BLACK, 4, 7);
		blackKing = (King) board[4][7];
		for (int i = 0; i < size; i++) {
			board[i][6] = new Pawn(BLACK, i, 6);
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 2; j < 6; j++) {
				board[i][j] = null;
			}
		}

		whiteKing.initializeThreats();
		history.clear();
	}

	/**
	 * Getter method for the board.
	 * 
	 * @return A two dimensional array which represents the board.
	 */
	public Piece[][] getBoard() {
		return board;
	}

	/**
	 * Determines if the space is empty or not
	 * 
	 * @param row
	 *            the row to check.
	 * @param col
	 *            the column to check.
	 * @return True if the space is empty.
	 */
	public boolean spaceIsEmpty(int row, int col) {
		if (board[row][col] == null) {
			return true;
		}
		return false;
	}

	/**
	 * Switch the current player.
	 */
	public void switchPlayer() {
		currentPlayer = !currentPlayer;
	}

	/**
	 * Places the given piece at a certain location on the board.
	 * 
	 * @param coordinate
	 *            The location the piece will be added to.
	 * @param chessPiece
	 *            The piece which will be moved.
	 */

	public void setBoard(Point coordinate, Piece chessPiece) {

		setBoard(coordinate.x, coordinate.y, chessPiece);

	}

	/**
	 * Places the given piece at a certain location on the board.
	 * 
	 * @param coordinate
	 *            The location the piece will be added to.
	 * @param chessPiece
	 *            The piece which will be moved.
	 */

	public void setBoard(int x, int y, Piece chessPiece) {

		board[x][y] = chessPiece;
		if (chessPiece != null) {
			chessPiece.setCoordinates(x, y);
		}

	}

	/**
	 * Change the board according to the move object. Returns the board after
	 * the change has been made.
	 * 
	 * @param m
	 *            the move made to the board
	 * @return the board after the move is made
	 */
	public Board makeMove(Move m) {

		// remove expired enPassants
		removeEnPassants();

		Piece movedPiece = m.movedPiece;
		// set original position to null
		setBoard(m.startRow, m.startCol, null);

		// capture piece
		if (m.capturedPiece != null) {
			setBoard(m.capturedPiece.row, m.capturedPiece.col, null);
		}

		// castling
		if (m.leftCastle) {
			setBoard(3, movedPiece.col, getPiece(0, movedPiece.col));
			setBoard(0, movedPiece.col, null);
		} else if (m.rightCastle) {
			setBoard(5, movedPiece.col, getPiece(7, movedPiece.col));
			setBoard(7, movedPiece.col, null);
		}

		// check for enPassant
		if ((movedPiece.color == BLACK && m.startCol == 6 && m.endCol == 4 && movedPiece.identity == PAWN)
				|| (movedPiece.color == WHITE && m.startCol == 1
						&& m.endCol == 3 && movedPiece.identity == PAWN)) {

			Pawn current = (Pawn) movedPiece;
			current.checkEnPassant();
		}

		// update blackKing and whiteKing
		if (movedPiece.identity == KING) {

			if (movedPiece.color == WHITE) {
				whiteKing = (King) movedPiece;
			} else {
				blackKing = (King) movedPiece;
			}

		}

		// move the piece to the desired position
		setBoard(m.endRow, m.endCol, movedPiece);
		// add to history
		history.addElement(m);

		switchPlayer();
		// update to Piece class
		Piece.updateBoard(this);
		updateThreats();
		return this;
	}

	/**
	 * Returns a list of all possible moves for one player. This method is used
	 * for AO to generate all moves.
	 * 
	 * @param aiColor
	 *            the color of the AI
	 * @return a list of move objects
	 */
	public List<Move> generateMoves(boolean aiColor) {

		List<Move> allPossibleMoves = new ArrayList<Move>();
		// go through the board and generate all possible moves
		if (aiColor) {
			for (Piece[] newRow : board) {
				for (Piece nextPiece : newRow) {
					if (nextPiece != null && nextPiece.color == WHITE) {
						allPossibleMoves.addAll(nextPiece.generateMoves());
					}
				}
			}
		} else {
			for (Piece[] newRow : board) {
				for (Piece nextPiece : newRow) {
					if (nextPiece != null && nextPiece.color == BLACK) {
						allPossibleMoves.addAll(nextPiece.generateMoves());
					}
				}
			}
		}
		return allPossibleMoves;
	}

	/**
	 * Evaluates the current Board positions
	 * 
	 * @param AIColor
	 *            true if the AI is white and false if it's black
	 * @param isEven
	 *            true if the last ply searched is the AI's move and false if
	 *            not
	 * @return the evaluation for the Board
	 */

	public double evaluate(boolean AIColor, boolean isEven) {
		double score = 0;
		int[] pieceCounts = new int[6];
		int[] threatsCounts = new int[6];
		// int[] mobilityCounts = new int[6];
		// Iterates through the Board
		for (Piece[] newRow : board) {
			for (Piece nextPiece : newRow) {
				if (nextPiece != null) {
					int ID = nextPiece.identity;
					if (AIColor == nextPiece.color) {
						// Counts the pieces
						pieceCounts[ID]++;
						// Counts the mobility
						// mobilityCounts[ID] +=
						// nextPiece.generateMoves().size();
						// Counts the threats
						if (nextPiece.isPositionUnderThreat()) {
							threatsCounts[ID]++;
						}
					} else {
						// Counts the pieces
						pieceCounts[ID]--;
						// Counts the mobility
						// mobilityCounts[ID] -=
						// nextPiece.generateMoves().size();
						// Counts the threats
						if (nextPiece.isPositionUnderThreat()) {
							threatsCounts[ID]--;
						}
					}
				}
			}
		}
		// If number of plies is even,
		if (isEven) {
			for (int loopCount = 0; loopCount < 6; loopCount++) {
				score += pieceCounts[loopCount] * pieceOwn[loopCount];
				// score += mobilityCounts[loopCount] *
				// pieceMobility[loopCount];
				score += threatsCounts[loopCount] * pieceThreats[loopCount];
			}
			// If its odd, counts threats as potential losses of pieces
		} else {
			for (int loopCount = 0; loopCount < 6; loopCount++) {
				score += pieceCounts[loopCount] * pieceOwn[loopCount];
				// score += mobilityCounts[loopCount] *
				// pieceMobility[loopCount];
				score -= threatsCounts[loopCount] * pieceOwn[loopCount];
			}
		}
		return score;
	}

	/**
	 * Undo the previous move.
	 */
	public void undoMove() {

		if (history.getSize() > 0) {

			Move previousMove = (Move) history.remove(history.getSize() - 1);
			Piece movedPiece = previousMove.movedPiece;

			// Change promoted piece back to pawns
			if (previousMove.promoted) {
				setBoard(previousMove.startRow, previousMove.startCol,
						previousMove.beforePromotion);
			} else {
				// Move piece back to previous position
				setBoard(previousMove.startRow, previousMove.startCol,
						movedPiece);
				// If there is castling occurred, move rooks back and set rook
				// back to unmoved
				if (previousMove.castled) {
					Piece rook;
					if (previousMove.leftCastle) {
						rook = getPiece(3, movedPiece.col);
						setBoard(0, movedPiece.col, rook);
						setBoard(3, movedPiece.col, null);
						rook.hasMoved = false;
					} else if (previousMove.rightCastle) {
						rook = getPiece(5, movedPiece.col);
						setBoard(7, movedPiece.col, rook);
						setBoard(5, movedPiece.col, null);
						rook.hasMoved = false;
					}
					// Set king back to unmoved
					movedPiece.hasMoved = false;
				}

			}

			// Set empty spaces
			setBoard(previousMove.endRow, previousMove.endCol, null);
			// Return captured pieces
			if (previousMove.capturedPiece != null) {
				Piece captured = previousMove.capturedPiece;
				setBoard(captured.row, captured.col, captured);
				if (movedPiece.identity == PAWN) {
					if (previousMove.startCol == previousMove.capturedPiece.col) {
						// set canEnPassant back
						if (previousMove.capturedPiece.row
								- previousMove.startRow > 0) {
							Pawn p = (Pawn) movedPiece;
							p.canEnPassantRight=true;
						} else {
							Pawn p = (Pawn) movedPiece;
							p.canEnPassantLeft=true;
						}
					}
				}
			}

			// update whiteKing and blackKing
			if (movedPiece.identity == KING) {

				if (movedPiece.color == WHITE) {
					whiteKing = (King) movedPiece;
				} else {
					blackKing = (King) movedPiece;
				}

			}

			switchPlayer();
			// update threats to Piece class
			updateThreats();
		} else {
			JOptionPane
					.showMessageDialog(null,
							"The board is at starting position. No more undos allowed.");
		}

	}

	/**
	 * Creates a window which allows the player to choose the piece he or she
	 * wants through pawn promotion.
	 * 
	 * @param colorOfPiece
	 *            The color of the new piece.
	 * @return a new integer which is determined by the player's choice of
	 *         piece.
	 */
	public int selectPromotion(boolean colorOfPiece) {
		Object[] option = new Object[4];
		// Displays the pictures of piece choices.
		if (colorOfPiece == WHITE) {
			option[0] = new ImageIcon("image\\queenw.gif");
			option[1] = new ImageIcon("image\\rookw.gif");
			option[2] = new ImageIcon("image\\bishopw.gif");
			option[3] = new ImageIcon("image\\knightw.gif");
		} else {
			option[0] = new ImageIcon("image\\queenb.gif");
			option[1] = new ImageIcon("image\\rookb.gif");
			option[2] = new ImageIcon("image\\bishopb.gif");
			option[3] = new ImageIcon("image\\knightb.gif");
		}
		int choice = JOptionPane.showOptionDialog(null, "Promote pawn to:", "",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, option, option[0]);
		return choice;
	}

	/**
	 * Removes all expired enPassant moves from the board.
	 */
	public void removeEnPassants() {

		for (int checkCol = 0; checkCol < 8; checkCol++) {
			for (int checkRow = 0; checkRow < 8; checkRow++) {

				if (board[checkRow][checkCol] != null
						&& getPiece(checkRow, checkCol).identity == PAWN) {
					Pawn currentPiece = (Pawn) getPiece(checkRow, checkCol);
					currentPiece.canEnPassantLeft = false;
					currentPiece.canEnPassantRight = false;
				}
			}
		}
	}

	/**
	 * Moves a piece to selected position
	 * @param selectedPiece
	 *            Moves a piece from its current location to one corresponding
	 *            to the input parameters
	 * @param toX
	 *            The x position where the piece will be moved to.
	 * @param toY
	 *            The y position where the piece will be moved to.
	 */
	public void moveTo(Piece selectedPiece, int toX, int toY) {

		boolean moveRecorded = false;
		removeEnPassants();

		if (selectedPiece.identity == PAWN) {

			if (toY == 7 || toY == 0) {
				Piece beforePromotion = selectedPiece;
				selectedPiece = new Queen(selectedPiece.color,
						selectedPiece.row, selectedPiece.col);
				int choice = selectPromotion(selectedPiece.color);
				
				// Pawn promotion. The user decides on which promotion he will
				// give the pawns.
				switch (choice) {
				case 0:
					selectedPiece = new Queen(selectedPiece.color,
							selectedPiece.row, selectedPiece.col);
					break;
				case 1:
					selectedPiece = new Rook(selectedPiece.color,
							selectedPiece.row, selectedPiece.col);
					break;
				case 2:
					selectedPiece = new Bishop(selectedPiece.color,
							selectedPiece.row, selectedPiece.col);
					break;
				case 3:
					selectedPiece = new Knight(selectedPiece.color,
							selectedPiece.row, selectedPiece.col);
					break;
				}
				history.addElement(new Move(toX, toY, selectedPiece, getPiece(
						toX, toY), beforePromotion));
				moveRecorded = true;
				// Remove enemy pieces when performing an enPassant
			} else if (toX != selectedPiece.row && getPiece(toX, toY) == null) {
				if (selectedPiece.color == WHITE) {
					history.addElement(new Move(toX, toY, selectedPiece,
							getPiece(toX, 4), false));
					moveRecorded = true;
					setBoard(toX, 4, null);

				} else {
					history.addElement(new Move(toX, toY, selectedPiece,
							getPiece(toX, 3), false));
					moveRecorded = true;
					setBoard(toX, 3, null);
				}
				// check for enPassant
			} else if ((selectedPiece.col == 6 && selectedPiece.color == BLACK
					&& toY == 4 && selectedPiece.identity == PAWN)
					|| (selectedPiece.col == 1 && selectedPiece.color == WHITE
							&& toY == 3 && selectedPiece.identity == PAWN)) {
				Pawn current = (Pawn) selectedPiece;
				current.checkEnPassant();

			}
		}

		// Moves the rook whenever a king castles to the left.
		if (selectedPiece.identity == KING && selectedPiece.row == 4) {
			if (toX == 2) {
				setBoard(3, selectedPiece.col, getPiece(0, selectedPiece.col));
				setBoard(0, selectedPiece.col, null);
				history.addElement(new Move(toX, toY, selectedPiece, null, true));
				moveRecorded = true;
				// Moves the rook whenever a king castles to the right.
			} else if (toX == 6) {
				setBoard(5, selectedPiece.col, getPiece(7, selectedPiece.col));
				setBoard(7, selectedPiece.col, null);
				history.addElement(new Move(toX, toY, selectedPiece, null, true));
				moveRecorded = true;
			}

		}

		// add to history
		if (!moveRecorded) {
			history.addElement(new Move(toX, toY, selectedPiece, getPiece(toX,
					toY), false));
		}
		// move piece to desired position
		this.setBoard(toX, toY, selectedPiece);

		// mark king and rook as moved so that no more castling is allowed
		if (selectedPiece.identity == ROOK || selectedPiece.identity == KING) {
			selectedPiece.hasMoved = true;
		}

		// Switch turn
		switchPlayer();

		// update to Piece class
		updateThreats();
		Piece.updateBoard(this);

	}

	/**
	 * Update the threats of current board to the Piece class.
	 */
	public void updateThreats() {

		if (currentPlayer == WHITE) {
			if (whiteKing == null) {
				Piece.threatsToKing = new Piece[2];
			} else {
				whiteIsInCheck = whiteKing.isPositionUnderThreat();
			}
		} else {
			if (blackKing == null) {
				Piece.threatsToKing = new Piece[2];
			} else {
				blackIsInCheck = blackKing.isPositionUnderThreat();
			}

		}
	}

	/**
	 * Converts a board to a string for debugging purposes. Empty spaces have a
	 * 0, spaces with a piece have their first letter listed (knight is 'k',
	 * King is 'K')
	 */
	public String toString() {

		StringBuffer printBoard = new StringBuffer();

		for (int row = 7; row >= 0; row--) {
			for (int col = 0; col < 8; col++) {
				if (board[col][row] != null)
					printBoard.append("" + board[col][row] + " ");
				else
					printBoard.append(0 + " ");
			}
			printBoard.append("\n");
		}

		return printBoard.toString();

	}

	/**
	 * Finds the piece at the given position on the board.
	 * 
	 * @param row
	 *            The row the piece is located in.
	 * @param col
	 *            The column the piece is located in.
	 * @return The piece at the specified location on the board. If the space is
	 *         empty, the program will return a null piece.
	 */
	public Piece getPiece(int row, int col) {
		return board[row][col];
	}

	/**
	 * Determines if there is a checkmate on the board.
	 * 
	 * @param color
	 *            The color which will be checked to see if it is in checkmate.
	 * @param threatsToKing
	 *            An array of any Pieces which place the king in check.
	 * @return True if there is a checkmate, False if there is not
	 */

	public boolean isCheckmate(boolean color) {
		if (color == WHITE) {
			return whiteIsInCheck && noPossibleMoves(WHITE);
		} else {

			return blackIsInCheck && noPossibleMoves(BLACK);
		}
	}

	/**
	 * Determines if there is a stalemate on the board.
	 * 
	 * @param color
	 *            The color which will be checked to see if it has a stalemate.
	 * @param threatsToKing
	 *            An array of threats to the King.
	 * @return True if there is a stalemate. False if there is not.
	 */
	public boolean isStalemate(boolean color) {
		if (color == WHITE) {
			return !whiteIsInCheck && noPossibleMoves(WHITE);
		} else {

			return !blackIsInCheck && noPossibleMoves(BLACK);
		}
	}

	/**
	 * Determines if there are moves on the board or not for the given color.
	 * 
	 * @param color
	 *            The color of the piece which will be checked.
	 * @param threatsToKing
	 *            An array of threats to the king.
	 * @return False if a single move is found for the specified color. True if
	 *         no moves are found.
	 */
	private boolean noPossibleMoves(boolean color) {

		// Search through the board and determines if there is a single move or
		// not.
		for (int checkRow = 0; checkRow < 8; checkRow++) {
			for (int checkCol = 0; checkCol < 8; checkCol++) {
				Piece currentPiece = board[checkRow][checkCol];
				if (currentPiece != null && currentPiece.color == color
						&& !currentPiece.generateValidMoves().isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Return the move history of the board
	 * 
	 * @return A DefaultListModel containing all moves made
	 */
	public DefaultListModel getHistory() {
		return history;
	}

}
