import java.util.List;

/**
 * An object that enables AI and searches for the best Move
 */

public class AI {

	private static int noOfDeepness = 4;
	private Board board;
	public Move bestMove;
	public boolean AIColor;

	/**
	 * Create a new AI object which stores the board and the color of AI
	 * 
	 * @param b
	 *            the current Board object
	 */
	public AI(Board b, boolean color) {
		this.board = new Board(b, false);
		AIColor = color;
	}

	/**
	 * Generates the best Move possible given the current Board position
	 * 
	 * @return the best Move found
	 */
	public Move getBestMove() {
		Move bestMove = new Move();
		double currentScore = -1000000;
		// Checks if the king is in check
		if (AIColor == false) {
			board.blackKing.isPositionUnderThreat();
		} else {
			board.whiteKing.isPositionUnderThreat();
		}
		// Generates the moves for first ply
		List<Move> firstPlyMoves = board.generateMoves(AIColor);

		for (Move nextMove : firstPlyMoves) {

			double score = alphaBetaMinimax(board.makeMove(nextMove), 1, false,
					-1000000, 1000000);
			board.undoMove();

			// If the score of the node is larger than the highest score so far
			if (score > currentScore) {
				// Saves the score and Move
				bestMove = nextMove;
				currentScore = score;
			}
		}

		return bestMove;
	}

	/**
	 * 
	 * @param b
	 *            the generated future Board
	 * @param currentPly
	 *            the level that it is currently searching
	 * @param isAITurn
	 *            true it's AI's turn and false if not
	 * @param alpha
	 *            the lower bound for the possible score to be considered
	 * @param beta
	 *            the upper bound for the possible score to be considered
	 * @return the lower bound for the possible score to be considered
	 */
	public double alphaBetaMinimax(Board b, int currentPly, boolean isAITurn,
			double alpha, double beta) {

		// If reached the node, evaluates board
		if (currentPly == noOfDeepness) {
			if (currentPly % 2 == 0) {
				return b.evaluate(AIColor, true);
			} else {
				return b.evaluate(AIColor, false);
			}
		}
		currentPly++;

		List<Move> possibleMoves;

		// If it's AI's turn
		if (currentPly % 2 == 1) {
			// Generates the possible Moves and update the king's status
			b.blackKing.isPositionUnderThreat();
			possibleMoves = b.generateMoves(AIColor);
			// For each possible Move, create a new Board and make the Move
			for (Move m : possibleMoves) {
				// Call the method recursively
				double result = alphaBetaMinimax(b.makeMove(m), currentPly,
						false, alpha, beta);
				b.undoMove();

				// If the score larger than the lower bound so far, store it

				if (result > alpha) {
					alpha = result;
				}
				// If cut-off is possible, return alpha
				if (alpha >= beta) {
					return alpha;
				}
			}
			return alpha;
		} else {

			b.whiteKing.isPositionUnderThreat();
			possibleMoves = b.generateMoves(!AIColor);
			for (Move m : possibleMoves) {

				double result = alphaBetaMinimax(b.makeMove(m), currentPly,
						true, alpha, beta);
				b.undoMove();
				// If the score lower than the upper bound so far, store it
				if (result < beta) {
					beta = result;
				}
				// If cut-off is possible, return beta
				if (beta <= alpha) {
					return beta;
				}
			}
			return beta;
		}
	}

	/**
	 * Change the number of plies to look ahead
	 * 
	 * @param noOfPlies
	 *            the number of plies
	 */
	public static void updateNoOfPlies(int noOfPlies) {
		noOfDeepness = noOfPlies;
	}

	/**
	 * Get the number of plies to look ahead
	 * 
	 * @return the current number of plies to look ahead
	 */
	public static int getNoOfPlies() {
		return noOfDeepness;
	}

}
