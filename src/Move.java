/**
 * 
 */

public class Move {

	private final String[] ROW_IN_LETTER = { "a", "b", "c", "d", "e", "f", "g",
			"h" };

	public int startRow;
	public int endRow;
	public int startCol;;
	public int endCol;
	public int evaluation;
	public Piece movedPiece;
	public Piece capturedPiece;
	public Piece beforePromotion;
	public boolean promoted;
	public boolean castled;
	public boolean leftCastle;
	public boolean rightCastle;

	public Move() {

	}

	// Creates a new Move with starting coordinates (sr,sc) and ending
	// coordinates (er,ec)
	// and piece p being moved
	/**
	 * Create a new Move object which stores one single move that each side has
	 * made
	 * 
	 * 
	 * @param er
	 *            end row of the moved piece
	 * @param ec
	 *            end column of the moved piece
	 * @param moved
	 *            the piece moved
	 * @param captured
	 *            the piece captured during the move
	 * @param castled
	 *            boolean indicating whether the current move is a castling move
	 */
	public Move(int er, int ec, Piece moved, Piece captured, boolean castled) {
		endRow = er;
		endCol = ec;
		movedPiece = moved.getCopyOf();
		startRow = movedPiece.row;
		startCol = movedPiece.col;
		if (captured == null) {
			this.capturedPiece = null;
		} else {
			capturedPiece = captured.getCopyOf();
		}
		promoted = false;
		beforePromotion = null;
		this.castled = castled;
		leftCastle = false;
		rightCastle = false;
		if (castled) {
			if (endRow - startRow > 0) {
				rightCastle = true;
			} else {
				leftCastle = true;
			}
		}
		
		

	}

	/**
	 * Create a new Move object which stores a promotion move (particularly used
	 * when promotion occurred)
	 * 
	 * @param er
	 *            end row of the moved piece
	 * @param ec
	 *            end column of the moved piece
	 * @param moved
	 *            the piece moved by the player
	 * @param captured
	 *            the piece captured during move
	 * @param beforePromotion
	 *            the piece before the promotion (pawn)
	 */
	public Move(int er, int ec, Piece moved, Piece captured,
			Piece beforePromotion) {

		endRow = er;
		endCol = ec;
		movedPiece = moved.getCopyOf();
		startRow = movedPiece.row;
		startCol = movedPiece.col;
		if (captured == null) {
			this.capturedPiece = null;
		} else {
			capturedPiece = captured.getCopyOf();
		}
		promoted = true;
		this.beforePromotion = beforePromotion.getCopyOf();
		castled = false;
		leftCastle = false;
		rightCastle = false;
	}

	// Compares the score of this move to move m (after scores were calculated)
	public int compareTo(Object objCompared) {
		return (int) (((Move) objCompared).evaluation * 1000 - 1000 * evaluation);
	}

	public String toString() {
		String notation = "";

		// black's turn
		if (movedPiece.color == false) {
			notation += "...";
		}

		// castling
		if (rightCastle) {
			notation += "O-O";
		} else if (leftCastle) {
			notation += "O-O-O";
			// promotion
		} else if (promoted) {
			notation += ROW_IN_LETTER[endRow] + (endCol + 1) + "=" + movedPiece;

		} else {

			// pawn
			if (movedPiece.identity == 0) {
				if (startRow != endRow) {
					notation += ROW_IN_LETTER[startRow];
				}
			} else {
				notation += movedPiece.toString();
			}

			// capture made
			if (capturedPiece != null) {
				notation += "x";
			}

			// position
			notation += ROW_IN_LETTER[endRow] + (endCol + 1);

			// indicates enPassant
			if (capturedPiece != null && capturedPiece.col != endCol) {
				notation += "e.p.";
			}
		}

		return notation;
	}
}
