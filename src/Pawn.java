import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

public class Pawn extends Piece {

	protected boolean canEnPassantLeft;
	protected boolean canEnPassantRight;

	/**
	 * Creates a pawn object for a standard game of chess at a specific position
	 * on the board. The pawn will have all the standard behavior and be able to
	 * enPassant only when it is legal.
	 * 
	 * @param color
	 *            The color of the pawn.
	 * @param row
	 *            The row the pawn starts at.
	 * @param col
	 *            The column the pawn starts at.
	 */
	public Pawn(boolean color, int row, int col) {

		// Sets the initial conditions for a pawn object.
		identity = PAWN;
		canEnPassantLeft = false;
		canEnPassantRight = false;

		this.color = color;
		setCoordinates(row, col);

		if (color == WHITE) {
			image = new ImageIcon("image\\pawnw.gif").getImage();
		} else {
			image = new ImageIcon("image\\pawnb.gif").getImage();
		}
	}

	public Set<Point> generateValidMoves() {

		Set<Point> validMoves = new HashSet<Point>();

		// If the king is under two threats, no moves can be done.
		if (threatsToKing[1] != null) {
			return validMoves;
		}

		// Check if the pawn is protecting king from a threat
		boolean guardsKing = isProtectingKing();

		// If a single piece is putting the king in check, and the pawn is
		// currently blocking the king from another potential threat. No moves
		// can be done.
		if (threatsToKing[0] != null && guardsKing) {

			return validMoves;
		}

		int checkRow = row;
		int checkCol = col;

		int colShift;

		// determine if the piece would move in which direction
		if (this.color == WHITE) {
			colShift = 1;
		} else {
			colShift = -1;
		}

		checkCol += colShift;

		// generate forward moves
		if (onGrid(checkRow, checkCol) && isEmpty(checkRow, checkCol)) {
			validMoves.add(new Point(checkRow, checkCol));

			if (this.color == WHITE && col == 1
					&& isEmpty(checkRow, checkCol + 1)) {
				validMoves.add(new Point(checkRow, checkCol + 1));
			} else if (this.color == BLACK && col == 6
					&& isEmpty(checkRow, checkCol - 1)) {
				validMoves.add(new Point(checkRow, checkCol - 1));
			}
		}

		checkCol = col;
		checkRow = row - 1;

		if (color == WHITE) {
			checkCol++;
		} else {
			checkCol--;
		}
		// left capture
		if (pawnCaptureCheck(checkRow, checkCol)) {
			validMoves.add(new Point(checkRow, checkCol));
		}

		checkRow += 2;

		// right capture
		if (pawnCaptureCheck(checkRow, checkCol)) {
			validMoves.add(new Point(checkRow, checkCol));
		}

		// Determine en passant left.
		if (canEnPassantLeft ) {
			validMoves.add(new Point(row - 1, col + colShift));
			// System.out.println(row+" "+col);
		}

		// Determine en passant right.
		if (canEnPassantRight ) {
			validMoves.add(new Point(row + 1, col + colShift));
			// System.out.println(row+" "+col);
		}

		// if the king is under one threat and this pawn is not protecting the
		// king at the moment, generate the moves that will protect the king

		if (threatsToKing[0] != null && !guardsKing) {
			validMoves.retainAll(kingProtectingMoves(threatsToKing[0]));
			return validMoves;
		}

		// generates all moves in which the pawn will not endanger the king
		// assuming it is standing between the king and a potential threat.

		if (guardsKing) {
			validMoves.retainAll(kingBlockerMoves());
			return validMoves;
		}

		return validMoves;

	}

	public List<Move> generateMoves() {
		Set<Point> storage = this.generateValidMoves();
		ArrayList<Move> moves = new ArrayList<Move>(storage.size());

		for (Point next : storage) {
			// promotion
			if (next.y == 0 || next.y == 7) {
				moves.add(new Move(next.x, next.y, new Queen(color, row, col),
						getPiece(next.x, next.y), this));

				// enPassent
			} else if (next.x != this.row && isEmpty(next.x, next.y)) {
				if (color == WHITE) {
					moves.add(new Move(next.x, next.y, this,
							getPiece(next.x, 4), false));
				} else {
					moves.add(new Move(next.x, next.y, this,
							getPiece(next.x, 3), false));
				}
			} else {
				// normal move
				moves.add(new Move(next.x, next.y, this, getPiece(next.x,
						next.y), false));
			}
		}
		return moves;
	}

	private boolean pawnCaptureCheck(int checkRow, int checkCol) {
		return onGrid(checkRow, checkCol) && !isEmpty(checkRow, checkCol)
				&& isEnemy(checkRow, checkCol);
	}

	public void checkEnPassant() {

		// white enPassants

		if (color == BLACK) {
			if (onGrid(this.row + 1, 4) && !isEmpty(this.row + 1, 4)
					&& getPiece(this.row + 1, 4).identity == PAWN
					&& isEnemy(this.row + 1, 4)) {
				Pawn other = (Pawn) getPiece(this.row + 1, 4);
				other.canEnPassantLeft = true;
			}
			if (onGrid(this.row - 1, 4) && !isEmpty(this.row - 1, 4)
					&& getPiece(this.row - 1, 4).identity == PAWN
					&& isEnemy(this.row - 1, 4)) {
				Pawn other = (Pawn) getPiece(this.row - 1, 4);
				other.canEnPassantRight = true;
			}
		}

		// Black enPassants

		else if (color == WHITE) {
			if (onGrid(this.row + 1, 3) && !isEmpty(this.row + 1, 3)
					&& getPiece(this.row + 1, 3).identity == PAWN
					&& isEnemy(this.row + 1, 3)) {
				Pawn other = (Pawn) getPiece(this.row + 1, 3);
				other.canEnPassantLeft = true;
			}
			if (onGrid(this.row - 1, 3) && !isEmpty(this.row - 1, 3)
					&& getPiece(this.row - 1, 3).identity == PAWN
					&& isEnemy(this.row - 1, 3)) {
				Pawn other = (Pawn) getPiece(this.row - 1, 3);
				other.canEnPassantRight = true;
			}
		}

	}

	/**
	 * Using the given location of the enemy pawn which will be captured and
	 * determine if it will be allowed. this method assumes the enPassant is
	 * possible without regard to the king, and ensures that it will not be in
	 * check afterwards, constituting a safe enPassant.
	 * 
	 * @param enemyPawnRow
	 *            The row of the enemy pawn which will be captured.
	 * @param enemyPawnCol
	 *            The column of the enemy pawn which will be captured.
	 * @return true if the enPassant can be made without endangering the king.
	 *//*
	private boolean safeEnPassant(int enemyPawnRow, int enemyPawnCol) {

		// Finds the direction of the enemy pawn to the allied king.
		Piece possibleEnemyBlocker = getPiece(enemyPawnRow, enemyPawnCol);

		// if (possibleEnemyBlocker == null){
		// return false;
		// }
		int[] dir = possibleEnemyBlocker.directionToPiece(getKingOfSameColor());

		// If there is no specific direction to the king, then there is no way
		// moving this piece will have any effect on the game.
		if (dir == null) {
			return true;
		}

		// If the king is protected by another piece, moving this piece wont
		// matter.
		if (!possibleEnemyBlocker.firstPieceInDirection(dir).equals(
				getKingOfSameColor())) {
			return true;
		}

		// Determines if the threatening piece will cause a check if the piece
		// is moved.
		Piece challengingPiece = possibleEnemyBlocker
				.firstPieceInDirection(reverseDirection(dir));
		if (challengingPiece != null) {
			if (goesDiagonal(dir)
					&& (challengingPiece.identity == BISHOP || challengingPiece.identity == QUEEN)) {
				return false;
			} else if (!goesDiagonal(dir)
					&& (challengingPiece.identity == ROOK || challengingPiece.identity == QUEEN)) {
				return false;
			}
		}
		return true;
	}*/
	
	public Piece getCopyOf() {
		Pawn copy = new Pawn(color, row, col);
		copy.canEnPassantLeft = this.canEnPassantLeft;
		copy.canEnPassantRight = this.canEnPassantRight;
		return copy;

	}	

	public String toString() {
		return "P";
	}

}
