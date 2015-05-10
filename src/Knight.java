import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

public class Knight extends Piece {

	/**
	 * Creates and sets the initial variables for a knight object
	 * 
	 * @param color
	 *            The color of the knight.
	 * @param row
	 *            The row which the knight is located.
	 * @param col
	 *            The column which the knight is located.
	 */
	public Knight(boolean color, int row, int col) {
		// Sets the properties of the knight.
		setCoordinates(row, col);
		identity = KNIGHT;
		this.color = color;
		if (color == WHITE) {
			image = new ImageIcon("image\\knightw.gif").getImage();
		} else {
			image = new ImageIcon("image\\knightb.gif").getImage();
		}
	}

	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// if two opponent's pieces puts king in check, no moves can be done.
		if (threatsToKing[1] != null) {
			return validMoves;
		}

		// determine if the knight is protecting king from a potential threat
		boolean guardsKing = isProtectingKing();

		// if one enemy piece puts king in check and the knight is protecting
		// king from another threat, no moves can be done.
		if (threatsToKing[0] != null && guardsKing) {
			return validMoves;
		}

		//go through all eight positions that the knight can reach
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

				//add valid positions
				if (canMove(checkRow, checkCol)) {
					validMoves.add(new Point(checkRow, checkCol));
				}

			}
		}

		// if the king is under one threat and this knight is not protecting the
		// king at the moment, generate the moves that will protect the king
		
		if (threatsToKing[0] != null && !guardsKing) {
			validMoves.retainAll(kingProtectingMoves(threatsToKing[0]));
			return validMoves;
		}

		// if the knight is standing between the king and a potential threat,
		// generates all moves in which the pawn will not endanger the king
		if (guardsKing) {
			validMoves.retainAll(kingBlockerMoves());
			return validMoves;
		}

		return validMoves;

	}
	
	public Piece getCopyOf(){
		return new Knight (color, row, col);
	}

	public String toString() {
		return "N";
	}

}
