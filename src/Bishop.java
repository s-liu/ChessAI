import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class Bishop extends Piece {

	/**
	 * Creates a Bishop object and sets the initial properties upon start.
	 * 
	 * @param color
	 *            The color the bishop will be.
	 * @param row
	 *            the row the bishop will be placed in.
	 * @param col
	 *            the column the bishop will be placed in.
	 */
	public Bishop(boolean color, int row, int col) {

		// Sets the properties of the bishop.
		identity = BISHOP;
		setCoordinates(row, col);
		this.color = color;
		if (color == WHITE) {
			image = new ImageIcon("image\\bishopw.gif").getImage();
		} else {
			image = new ImageIcon("image\\bishopb.gif").getImage();
		}
	}

	/**
	 * Creates a list of all the possible moves for the current object.
	 * 
	 * @return A list of all the valid moves for the current object.
	 */
	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// if the king is in check by two enemy pieces, no moves can be done.
		if (threatsToKing[1] != null) {
			return validMoves;
		}

		// determine if the bishop is protecting the king
		boolean guardsKing = isProtectingKing();

		// if one piece is putting king in check while this bishop is protecting
		// from another potential threat, no moves can be done.
		if (threatsToKing[0] != null && guardsKing) {
			return validMoves;
		}

		boolean stop = false;

		// go through diagonal directions
		for (int i = 0; i < 4; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];
			stop = false;
			int checkRow = row;
			int checkCol = col;

			while (!stop) {

				checkRow = checkRow + dirX;
				checkCol = checkCol + dirY;

				// stop adding when piece of same colour is detected
				if (!onGrid(checkRow, checkCol)
						|| (!isEmpty(checkRow, checkCol) && !isEnemy(checkRow,
								checkCol))) {
					stop = true;
				} else {
					// add valid positions
					validMoves.add(new Point(checkRow, checkCol));
					// stop adding when a enemy piece is spotted
					if (!isEmpty(checkRow, checkCol)) {
						stop = true;
					}

				}
			}
		}

		// if only one piece puts king in check and the bishop is not protecting
		// the king at the moment, generate moves that will protect the king
		if (threatsToKing[0] != null && !guardsKing) {
			validMoves.retainAll(kingProtectingMoves(threatsToKing[0]));
			return validMoves;
		}

		// if the bishop is standing between the king and a potential threat,
		// generates all moves in which the pawn will not endanger the king
		if (guardsKing) {
			validMoves.retainAll(kingBlockerMoves());
			return validMoves;
		}

		return validMoves;
	}
	
	public Piece getCopyOf(){
		return new Bishop (color, row, col);
	}

	public String toString() {
		return "B";
	}

}
