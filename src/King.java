import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

public class King extends Piece {

	/**
	 * Creates and sets the initial variables for a king object
	 * 
	 * @param color
	 *            The color of the king.
	 * @param row
	 *            The row which the king is located.
	 * @param col
	 *            The column which the king is located.
	 */
	public King(boolean color, int row, int col) {

		// Sets the properties of the King.
		identity = KING;
		this.color = color;
		setCoordinates(row, col);
		hasMoved = false;
		if (color == WHITE) {
			image = new ImageIcon("image\\kingw.gif").getImage();
		} else {
			image = new ImageIcon("image\\kingb.gif").getImage();
		}
	}

	/**
	 * Determine if the King would be able to castle in the desired side
	 * 
	 * @param direction
	 *            LEFT - queenSide, RIGHT - kingSide
	 * @return true if the King is able to perform a castle maneuver to its
	 *         right. False if it cannot.
	 */
	private boolean canCastle(int[] direction) {

		// If the king has moved, it may not castle anymore.
		if (this.hasMoved) {
			return false;
		}

		int checkRow = row + direction[0];
		int checkCol = col + direction[1];

		Rook castleRook = null;

		// Finds the rook the king wants to castle with. if it is not in the
		// original location, you know the rook has moved and can no longer be
		// valid for a castle.
		if (direction == RIGHT && !isEmpty(7, this.col)
				&& getPiece(7, this.col).identity == ROOK) {
			castleRook = (Rook) getPiece(7, this.col);
		} else if (direction == LEFT && !isEmpty(0, this.col)
				&& getPiece(0, this.col).identity == ROOK) {
			castleRook = (Rook) getPiece(0, this.col);
		} else {
			return false;
		}

		// if the king is in check or has moved, it is not eligible for a
		// castle.
		if (castleRook.hasMoved || threatsToKing[0] != null) {
			return false;
		}

		// Makes the first two steps of the castle for the king.
		for (int i = 0; i < 2; i++) {
			if (!isEmpty(checkRow, checkCol)
					|| isPositionUnderThreat(checkRow, checkCol)) {
				return false;
			}
			checkRow += direction[0];
			checkCol += direction[1];
		}

		// Extends to the rook and ensures the rest of the spaces are empty and
		// a castle is valid.
		while (!(checkRow == castleRook.row && checkCol == castleRook.col)) {
			if (!isEmpty(checkRow, checkCol)) {
				return false;
			}

			checkRow += direction[0];
			checkCol += direction[1];
		}
		return true;
	}

	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// Go through all eight directions
		for (int i = 0; i < 8; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];

			int checkRow = row + dirX;
			int checkCol = col + dirY;

			// add position if the move is valid and will not puts king in
			// danger
			if (canMove(checkRow, checkCol)
					&& !isPositionUnderThreat(checkRow, checkCol)) {
				validMoves.add(new Point(checkRow, checkCol));
			}

		}

		// If there is a single threat to the king, castling cannot be done.
		if (threatsToKing[0] == null) {
			if (canCastle(RIGHT)) {
				validMoves.add(new Point(6, this.col));
			}
			if (canCastle(LEFT)) {
				validMoves.add(new Point(2, this.col));
			}
		}

		return validMoves;
	}

	public List<Move> generateMoves() {
		Set<Point> storage = this.generateValidMoves();
		ArrayList<Move> moves = new ArrayList<Move>(storage.size());
		
		for (Point next : storage) {
			
			if (next.x - row == 2 || next.x - row == -2) {
				//castling
				moves.add(new Move(next.x, next.y, this, null, true));
			} else {
				//normal moves
				moves.add(new Move(next.x, next.y, this, getPiece(next.x,
						next.y), false));
			}
		}
		return moves;
	}
	
	public Piece getCopyOf(){
		King copy = new King (color, row, col);
		copy.hasMoved = this.hasMoved;
		return copy;
	}

	public String toString() {
		return "K";
	}

}
