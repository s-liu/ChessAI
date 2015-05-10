import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class Rook extends Piece {

	/**
	 * Creates a rook object and sets its initial conditions.
	 * 
	 * @param color
	 *            The color of the rook object.
	 * @param row
	 *            The starting row of the rook object.
	 * @param col
	 *            The starting column of the Rook object
	 */
	public Rook(boolean color, int row, int col) {
		// Sets the initial properties for the rook.
		setCoordinates(row, col);
		identity = ROOK;
		hasMoved = false;
		this.color = color;
		if (color == WHITE) {
			image = new ImageIcon("image\\rookw.gif").getImage();
		} else {
			image = new ImageIcon("image\\rookb.gif").getImage();
		}

	}

	/**
	 * Creates a list of all the possible moves for the current object.
	 * 
	 * @return A list of all the valid moves for the current object.
	 */
	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// if two opponent's pieces put king in check, no possible move can be
		// done.
		if (threatsToKing[1] != null) {
			return validMoves;
		}

		// determine if the rook is blocking a potential threat from targeting
		// the king
		boolean guardsKing = isProtectingKing();

		// if the rook is protecting king and at the same time one opponent's
		// piece puts king in check, no moves can be done.
		if (threatsToKing[0] != null && guardsKing) {
			return validMoves;
		}

		// go through vertical and horizontal directions.
		for (int i = 4; i < 8; i++) {
			int dirX = EIGHT_DIRECTIONS[i][0];
			int dirY = EIGHT_DIRECTIONS[i][1];
			boolean stop = false;
			int checkRow = row;
			int checkCol = col;

			while (!stop) {

				checkRow = checkRow + dirX;
				checkCol = checkCol + dirY;

				// if a piece with same color is found, stop adding positions
				if (!onGrid(checkRow, checkCol)
						|| (!isEmpty(checkRow, checkCol) && !isEnemy(checkRow,
								checkCol))) {
					stop = true;
				} else {
					// add valid positions
					validMoves.add(new Point(checkRow, checkCol));
					// if a enemy piece is found, stop adding positions
					if (!isEmpty(checkRow, checkCol)) {
						stop = true;
					}

				}
			}
		}
		
		// if the king is under one threat and this rook is not protecting the
		// king at the moment, generate the moves that will protect the king
		if (threatsToKing[0] != null && !guardsKing) {
			validMoves.retainAll(kingProtectingMoves(threatsToKing[0]));
			return validMoves;
		}
		
		// if the rook is standing between the king and a potential threat,
		// generates all moves in which the pawn will not endanger the king
		if (guardsKing) {
			validMoves.retainAll(kingBlockerMoves());
			return validMoves;
		}

		return validMoves;
	}
	
	public Piece getCopyOf(){
		Rook copy = new Rook (color, row, col);
		copy.hasMoved = this.hasMoved;
		return copy;
	}

	public String toString() {
		return "R";
	}

}
