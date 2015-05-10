import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class Piece {

	protected final boolean BLACK = false;
	protected final boolean WHITE = true;
	protected static Board myBoard;
	protected static Piece[] threatsToKing;
	protected Image image;
	protected int row;
	protected int col;
	protected boolean color;
	protected boolean hasMoved;
	protected int identity;

	// directions
	protected final int[] BOTTOMLEFT = { -1, -1 };
	protected final int[] BOTTOMRIGHT = { 1, -1 };
	protected final int[] TOPLEFT = { -1, 1 };
	protected final int[] TOPRIGHT = { 1, 1 };
	protected final int[] BOTTOM = { 0, -1 };
	protected final int[] RIGHT = { 1, 0 };
	protected final int[] TOP = { 0, 1 };
	protected final int[] LEFT = { -1, 0 };
	protected final int[][] EIGHT_DIRECTIONS = { BOTTOMLEFT, TOPRIGHT,
			BOTTOMRIGHT, TOPLEFT, BOTTOM, RIGHT, TOP, LEFT };

	// identity numbers used to differentiate pieces
	protected final int PAWN = 0;
	protected final int BISHOP = 1;
	protected final int KNIGHT = 2;
	protected final int ROOK = 3;
	protected final int QUEEN = 4;
	protected final int KING = 5;

	/**
	 * Sets the coordinate on the board of an object.
	 * 
	 * @param setRow
	 *            Sets the row of the piece.
	 * @param setCol
	 *            Sets the column of the piece.
	 */
	public void setCoordinates(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Draw the image of piece on the screen
	 * 
	 * @param g
	 *            graphics context
	 * @param x
	 *            the x position of the image
	 * @param y
	 *            the y position of the image
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
	}

	/**
	 * Update the current board to the piece class
	 * 
	 * @param b
	 *            current board
	 */
	public static void updateBoard(Board b) {
		myBoard = b;

		// System.out.println(myBoard);
	}

	/**
	 * Determines if the given point is on the grid or not.
	 * 
	 * @param checkRow
	 *            the row where a piece will move to.
	 * @param checkCol
	 *            the column where the piece will move to.
	 * @return returns true if the piece is on the grid or not.
	 */
	public boolean onGrid(int checkRow, int checkCol) {

		return (checkRow < 8 && checkRow >= 0 && checkCol < 8 && checkCol >= 0);

	}

	/**
	 * Determines if the location on the board is empty or not.
	 * 
	 * @param checkRow
	 *            The row to check.
	 * @param checkCol
	 *            The column to check.
	 * @return True if the space is empty. False if it is not.
	 */
	public boolean isEmpty(int checkRow, int checkCol) {
		return myBoard.getPiece(checkRow, checkCol) == null;
	}

	/**
	 * Determines if the space is an enemy or not. (precondition) there is a
	 * piece on the space of the board.
	 * 
	 * @param checkRow
	 *            The row to check.
	 * @param checkCol
	 *            The column to check.
	 * @return true if the piece has a different color or false if they are the
	 *         same.
	 */
	public boolean isEnemy(int checkRow, int checkCol) {

		return myBoard.getPiece(checkRow, checkCol).color != this.color;
	}

	/**
	 * Determines if the piece can move to the given location assuming the move
	 * is allowed for the given piece.
	 * 
	 * @param checkRow
	 *            The row of the piece.
	 * @param checkCol
	 *            The column of the piece.
	 * @return true if the space is empty or an enemy.
	 */
	public boolean canMove(int checkRow, int checkCol) {
		return ((onGrid(checkRow, checkCol)) && (isEmpty(checkRow, checkCol) || isEnemy(
				checkRow, checkCol)));

	}

	/**
	 * Gets the piece at a given location.
	 * 
	 * @param row
	 *            The row in which the piece is located.
	 * @param col
	 *            The column in which the piece is located.
	 * @return The piece at the given location.
	 */
	protected Piece getPiece(int row, int col) {
		return myBoard.getPiece(row, col);
	}

	/**
	 * Gets the point the object is located at.
	 * 
	 * @return a Point object containing the location of the object.
	 */
	public Point getPoint() {
		return new Point(row, col);
	}

	/**
	 * Returns a copy of this piece object
	 * 
	 * @return the copy of the current piece object
	 */
	public abstract Piece getCopyOf();

	/**
	 * Returns a string representation of this piece object
	 * 
	 * @return the string representation
	 */
	public abstract String toString();

	/**
	 * Will generate all of the possible valid moves.
	 * 
	 * @return a set of all the possible moves for the object at the current
	 *         moment in time.
	 */
	public abstract Set<Point> generateValidMoves();

	/**
	 * Transforms the Set of Points to a List of Moves
	 * 
	 * @return the List of all possible Moves for this Piece
	 */
	public List<Move> generateMoves() {
		// Generates all the points this Piece can move to
		Set<Point> storage = this.generateValidMoves();
		ArrayList<Move> moves = new ArrayList<Move>(storage.size());
		// Transforms them into Move objects
		for (Point next : storage) {
			moves.add(new Move(next.x, next.y, this, getPiece(next.x, next.y),
					false));
		}
		return moves;
	}

	/**
	 * Reverses a direction.
	 * 
	 * @param dir
	 *            the direction which will be reversed.
	 * @return the direction which travels in the opposite direction.
	 */
	protected int[] reverseDirection(int[] dir) {

		int[] reverse = { dir[0] * -1, dir[1] * -1 };
		return reverse;
	}

	/**
	 * Determines if a piece is traveling diagonally or not.
	 * 
	 * @param dir
	 *            the direction to be determined
	 * @return true if the direction is diagonal, false otherwise
	 */
	protected boolean goesDiagonal(int[] dir) {
		return (dir[0] != 0 && dir[1] != 0);
	}

	/**
	 * Finds the direction of a piece from this piece. (will be used for move
	 * generation an limiting so that a move will not place the king in check)
	 * 
	 * @param other
	 *            The piece which we will find the direction to.
	 * 
	 * @return An array of two integers containing the direction of the king. If
	 *         it cannot be represented like this, it will return null;
	 */
	protected int[] directionToPiece(Piece other) {

		if (other == null) {
			return null;
		}

		int rowShift = other.row - this.row;
		int colShift = other.col - this.col;

		if (rowShift == 0) {
			if (colShift > 0) {
				return TOP;
			} else {
				return BOTTOM;
			}

		} else if (colShift == 0) {
			if (rowShift > 0) {
				return RIGHT;
			} else {
				return LEFT;
			}
		}

		if (Math.abs(rowShift) == Math.abs(colShift)) {
			if (rowShift > 0) {
				if (colShift > 0) {
					return TOPRIGHT;
				} else {
					return BOTTOMRIGHT;
				}
			} else {
				if (colShift > 0) {
					return TOPLEFT;
				} else {
					return BOTTOMLEFT;
				}
			}
		}

		return null;

	}

	/**
	 * Finds the first piece in a given direction from the the current piece
	 * 
	 * 
	 * @param rowDirection
	 *            the direction of the row in which to move.
	 * @param colDirection
	 *            the direction of the columns in which to move.
	 * @return the first piece in the specified direction
	 */
	protected Piece firstPieceInDirection(int[] dir) {

		int dirX = dir[0];
		int dirY = dir[1];

		int checkRow = row + dirX;
		int checkCol = col + dirY;

		while (onGrid(checkRow, checkCol)) {

			if (!isEmpty(checkRow, checkCol)) {
				return getPiece(checkRow, checkCol);
			}

			checkRow += dirX;
			checkCol += dirY;

		}

		return null;
	}

	/**
	 * Determines if the piece is protecting the king or not by serving as a
	 * piece impeding the path of another which will otherwise target the king.
	 * 
	 * @return true if the piece is blocking the king from check, false if not
	 */
	protected boolean isProtectingKing() {

		// There is no chance of protecting the king if the challenging piece is
		// pawn or knight

		int[] dirOfKing;

		dirOfKing = directionToPiece(getKingOfSameColor());

		if (dirOfKing != null) {

			Piece firstInDirection = firstPieceInDirection(dirOfKing);

			// check if the current piece is on the horizontal, vertical, or
			// diagonal direction to the king
			if (firstInDirection != null && firstInDirection.identity == KING
					&& !isEnemy(firstInDirection.row, firstInDirection.col)) {
				// finds the enemy piece that creates a potential threat to the
				// king
				int[] reverse = reverseDirection(dirOfKing);
				Piece challengingPiece = firstPieceInDirection(reverse);

				if (challengingPiece == null) {
					return false;
				}

				// determines if something is blocking the king horizontally or
				// vertically
				if (!goesDiagonal(reverse)
						&& (challengingPiece.identity == ROOK || challengingPiece.identity == QUEEN)
						&& isEnemy(challengingPiece.row, challengingPiece.col)) {
					return true;
					// determines if something is blocking the king diagonally.
				} else if (goesDiagonal(reverse)
						&& (challengingPiece.identity == BISHOP || challengingPiece.identity == QUEEN)
						&& isEnemy(challengingPiece.row, challengingPiece.col)) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * Return the king which has the same color as the current piece
	 * 
	 * @return the king of same color
	 */
	protected King getKingOfSameColor() {
		if (this.color == WHITE) {
			return myBoard.whiteKing;
		} else {
			return myBoard.blackKing;
		}
	}

	/**
	 * Generate all moves that can stop the challenging piece from targeting the
	 * king.
	 * 
	 * @param challengingPiece
	 *            the piece that is currently placing the king in check.
	 * @return a list of all the moves which will prevent the check.
	 */
	protected Set<Point> kingProtectingMoves(Piece challengingPiece) {

		Set<Point> moves = new HashSet<Point>();

		// capturing the challenging piece is one method of protecting king
		moves.add(challengingPiece.getPoint());
		int initial;
		int terminal;

		// There is no chance of protecting the king if the challenging piece is
		// pawn or knight

		// if the challenging piece is a rook or queen (in check either in
		// vertical or horizontal direction
		if (challengingPiece.identity == ROOK
				|| challengingPiece.identity == QUEEN) {
			// get the coordinates of both the king and the challenging piece
			int challengingRow = challengingPiece.row;
			int challengingCol = challengingPiece.col;
			int myKingRow = getKingOfSameColor().row;
			int myKingCol = getKingOfSameColor().col;

			// if the king is checked in vertical direction
			if (challengingRow == myKingRow) {
				// determine if the challenging piece is above or below the king
				if (challengingCol > myKingCol) {
					initial = myKingCol;
					terminal = challengingCol;
				} else {
					initial = challengingCol;
					terminal = myKingCol;
				}
				// add all positions between the two pieces
				for (int i = initial + 1; i < terminal; i++) {
					moves.add(new Point(challengingRow, i));
				}
				// if the king is checked in horizontal direction
			} else if (challengingCol == myKingCol) {
				// determine if the challenging piece is on the left of right of
				// the king
				if (challengingRow > myKingRow) {
					initial = myKingRow;
					terminal = challengingRow;
				} else {
					initial = challengingRow;
					terminal = myKingRow;
				}
				// add all positions in between the two
				for (int i = initial + 1; i < terminal; i++) {
					moves.add(new Point(i, challengingCol));
				}
			}
		}
		// if the challenging piece is a bishop or a queen (the king is in check
		// in diagonal direction)
		if (challengingPiece.identity == BISHOP
				|| challengingPiece.identity == QUEEN) {

			// get coordinates of two pieces
			int challengingRow = challengingPiece.row;
			int challengingCol = challengingPiece.col;
			int myKingRow = getKingOfSameColor().row;
			int myKingCol = getKingOfSameColor().col;

			// if the king is in check in northeast-southwest direction
			if (challengingRow - myKingRow == challengingCol - myKingCol) {
				int diff;
				// determine which one is at east, which one is at west
				if (challengingRow > myKingRow) {
					initial = myKingRow;
					diff = myKingRow - myKingCol;
					terminal = challengingRow;
				} else {
					initial = challengingRow;
					diff = challengingRow - challengingCol;
					terminal = myKingRow;
				}
				// add all positions in between
				for (int i = initial + 1; i < terminal; i++) {
					moves.add(new Point(i, i - diff));

				}
				// if the king is in check in northwest-southeast direction
			} else if (challengingRow + challengingCol == myKingRow + myKingCol) {
				int sum = myKingRow + myKingCol;
				// determine which piece is at east, which piece is at west
				if (challengingRow > myKingRow) {
					initial = myKingRow;

					terminal = challengingRow;
				} else {
					initial = challengingRow;

					terminal = myKingRow;
				}
				// add all positions between two pieces
				for (int i = initial + 1; i < terminal; i++) {
					moves.add(new Point(i, sum - i));

				}
			}

		}

		return moves;
	}

	/**
	 * Generates a list of moves which will ensure the king is not placed in
	 * check.
	 * 
	 * @return a list of all the possible moves which will protect the king if
	 *         it is the piece which is preventing the king from being in check.
	 */
	protected Set<Point> kingBlockerMoves() {

		Set<Point> validMoves = new HashSet<Point>();
		Piece myKing = getKingOfSameColor();
		// get the direction to king
		int[] dirOfKing = directionToPiece(myKing);
		// because this piece is blocking king from threats, reverse the
		// direction so now the direction points to the challenging piece
		int[] reverse = reverseDirection(dirOfKing);

		// get the challenging piece
		Piece challengingPiece = firstPieceInDirection(reverse);

		// Determines if the piece is blocking something horizontally
		// and generates the moves.
		int dirX = reverse[0];
		int dirY = reverse[1];

		boolean stop = false;
		int checkRow = myKing.row + dirX;
		int checkCol = myKing.col + dirY;

		// go through all spaces between the blocker piece and the challenging
		// piece add all positions in between
		while (!stop) {

			checkRow += dirX;
			checkCol += dirY;
			validMoves.add(new Point(checkRow, checkCol));
			if (checkRow == challengingPiece.row
					&& checkCol == challengingPiece.col) {
				stop = true;
			}

		}

		return validMoves;
	}

	/**
	 * Check if a position on board is the opponent's king
	 * 
	 * @param checkRow
	 *            the row of the position
	 * @param checkCol
	 *            the column of the position
	 * @return true if the position has a enemy king, false otherwise
	 */
	private boolean isEnemyKing(int checkRow, int checkCol) {
		return (onGrid(checkRow, checkCol) && !isEmpty(checkRow, checkCol)
				&& isEnemy(checkRow, checkCol) && getPiece(checkRow, checkCol).identity == KING);
	}

	/**
	 * Check if there is any piece targeting the given position. Update threats
	 * to king if the current piece is king.
	 * 
	 * @param row
	 *            the row of the given position
	 * @param col
	 *            the column of the given position
	 * @return true if the position if under threat, false if the position is
	 *         safe
	 */
	public boolean isPositionUnderThreat(int row, int col) {
		// vertical/horizontal
		boolean checkThreats = false;
		if (row == this.row && col == this.col && identity == KING) {
			initializeThreats();
			checkThreats = true;
		}
		for (int i = 0; i < 4; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];
			boolean stop = false;
			int checkRow = row + dirX;
			int checkCol = col + dirY;

			if (isEnemyKing(checkRow, checkCol)) {
				return true;
			}

			while (onGrid(checkRow, checkCol) && !stop) {
				if (!isEmpty(checkRow, checkCol)) {
					if (isEnemy(checkRow, checkCol)
							&& (getPiece(checkRow, checkCol).identity == QUEEN || getPiece(
									checkRow, checkCol).identity == BISHOP)) {
						if (checkThreats) {
							if (!addThreat(getPiece(checkRow, checkCol))) {
								return true;
							}
						} else {
							return true;
						}
						stop = true;
					} else if (!isEnemy(checkRow, checkCol)
							&& getPiece(checkRow, checkCol).identity == KING) {
						// do nothing (useful when checking the valid moves of
						// the king)
					} else {
						stop = true;
					}
				}
				checkRow = checkRow + dirX;
				checkCol = checkCol + dirY;
			}
		}

		for (int i = 4; i < 8; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];
			boolean stop = false;
			int checkRow = row + dirX;
			int checkCol = col + dirY;

			if (isEnemyKing(checkRow, checkCol)) {
				return true;
			}

			while (onGrid(checkRow, checkCol) && !stop) {
				if (!isEmpty(checkRow, checkCol)) {
					if (isEnemy(checkRow, checkCol)
							&& (getPiece(checkRow, checkCol).identity == QUEEN || getPiece(
									checkRow, checkCol).identity == ROOK)) {
						if (checkThreats) {
							if (!addThreat(getPiece(checkRow, checkCol))) {
								return true;
							}
						} else {
							return true;
						}
						stop = true;
					} else if (!isEnemy(checkRow, checkCol)
							&& getPiece(checkRow, checkCol).identity == KING) {
						// do nothing
					} else {
						stop = true;
					}
				}
				checkRow = checkRow + dirX;
				checkCol = checkCol + dirY;
			}
		}
		// Knight
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				int dirX = EIGHT_DIRECTIONS[i][0];
				int dirY = EIGHT_DIRECTIONS[i][1];
				if (j == 0) {
					dirX = dirX * 2;
				} else {
					dirY = dirY * 2;
				}
				int checkRow = row + dirX;
				int checkCol = col + dirY;
				if (onGrid(checkRow, checkCol) && !isEmpty(checkRow, checkCol)
						&& getPiece(checkRow, checkCol).identity == KNIGHT
						&& isEnemy(checkRow, checkCol)) {
					if (checkThreats) {
						if (!addThreat(getPiece(checkRow, checkCol))) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
		}

		// Pawn
		int checkCol;
		if (color == WHITE) {
			checkCol = col + 1;
		} else {
			checkCol = col - 1;
		}

		for (int checkRow = row + 1; checkRow >= row - 1; checkRow -= 2) {
			if (onGrid(checkRow, checkCol) && !isEmpty(checkRow, checkCol)
					&& getPiece(checkRow, checkCol).identity == PAWN
					&& isEnemy(checkRow, checkCol)) {
				if (checkThreats) {
					if (!addThreat(getPiece(checkRow, checkCol))) {
						return true;
					}
				} else {
					return true;
				}
			}
		}

		if (checkThreats) {
			if (threatsToKing[0] != null) {

				return true;
			}
		}

		return false;

	}

	/**
	 * Check if the current piece is at risk.
	 * 
	 * @return true if there is a enemy piece targeting the current piece, false
	 *         otherwise
	 */
	public boolean isPositionUnderThreat() {
		return isPositionUnderThreat(row, col);
	}

	/**
	 * Add the given piece to the list of pieces which targets the king
	 * 
	 * @param challengingPiece
	 *            the piece targeting the king
	 * @return false if the list is full (two pieces at most), true if there is
	 *         spare space in the list.
	 */
	private boolean addThreat(Piece challengingPiece) {
		if (threatsToKing[0] != null) {
			threatsToKing[1] = challengingPiece;
			return false;
		} else {
			threatsToKing[0] = challengingPiece;
			return true;
		}
	}

	/**
	 * Initializes the threats array in the piece class. This method will reset
	 * the value and/or ensure the array has been initialized. This is used in
	 * the new game method.
	 */
	protected void initializeThreats() {
		threatsToKing = new Piece[2];
	}

}
