import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class Queen extends Piece {

	/**
	 * Creates a queen object and sets its initial conditions.
	 * 
	 * @param color
	 *            The color of the queen object.
	 * @param row
	 *            The starting row of the queen object.
	 * @param col
	 *            The starting column of the queen object
	 */
	public Queen(boolean color, int row, int col) {
		// Sets the initial properties for the queen.
		identity = QUEEN;
		setCoordinates(row, col);
		this.color = color;
		if (color == WHITE) {
			image = new ImageIcon("image\\queenw.gif").getImage();
		} else {
			image = new ImageIcon("image\\queenb.gif").getImage();
		}
	}

	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// if two enemy pieces put king in check, no moves can be made.
		if (threatsToKing[1] != null) {
			return validMoves;
		}

		// determine if the queen is preventing another potential threats from
		// targeting king
		boolean guardsKing = isProtectingKing();

		// if the king is under one threat and the queen is protecting king from
		// another potential threat, no move can be made.
		if (threatsToKing[0] != null && guardsKing) {
			return validMoves;
		}

		// go through all eight directions
		for (int i = 0; i < 8; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];
			boolean stop = false;
			int checkRow = row;
			int checkCol = col;

			while (!stop) {

				checkRow = checkRow + dirX;
				checkCol = checkCol + dirY;

				// stop adding when piece with same colour is found
				if (!onGrid(checkRow, checkCol)
						|| (!isEmpty(checkRow, checkCol) && !isEnemy(checkRow,
								checkCol))) {
					stop = true;
				} else {
					// add valid moves
					validMoves.add(new Point(checkRow, checkCol));
					// stop adding when an enemy piece is found
					if (!isEmpty(checkRow, checkCol)) {
						stop = true;
					}

				}
			}
		}

		// if one piece puts king in check and the queen is not protecting
		// the king at the moment, generate moves that will protect the king
		if (threatsToKing[0] != null && !guardsKing) {
			validMoves.retainAll(kingProtectingMoves(threatsToKing[0]));
			return validMoves;
		}

		// if the queen is protecting the king from another potential threat,
		// generates all moves in which the pawn will not endanger the king
		if (guardsKing) {
			validMoves.retainAll(kingBlockerMoves());
			return validMoves;
		}

		return validMoves;
	}
	
	public Piece getCopyOf(){
		return new Queen (color, row, col);
	}

	public String toString() {
		return "Q";
	}

}
