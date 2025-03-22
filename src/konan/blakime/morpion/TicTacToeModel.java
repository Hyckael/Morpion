package konan.blakime.morpion;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author BLACKIME CHRISTIANNA && KONAN EVRARD HYCKAEL
 */

public class TicTacToeModel {
	private final static int BOARD_WIDTH = 3;
	private final static int BOARD_HEIGHT = 3;
	// nombre de pièce min à aligner pour gagner
	private final static int WINNING_COUNT = 3;

	private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);
	private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);
	private final ObjectProperty<Owner>[][] board = new ObjectProperty[BOARD_WIDTH][BOARD_HEIGHT];
	private final BooleanProperty[][] winningBoard = new BooleanProperty[BOARD_WIDTH][BOARD_HEIGHT];

	private TicTacToeModel() {
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
				winningBoard[i][j] = new SimpleBooleanProperty(false);
			}
		}
	}

	public static TicTacToeModel getInstance() {
		TicTacToeModelHolder.INSTANCE.restart();
		return TicTacToeModelHolder.INSTANCE;
	}

	private static class TicTacToeModelHolder {
		private static final TicTacToeModel INSTANCE = new TicTacToeModel();
	}

	public void restart() {
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				board[i][j].set(Owner.NONE);
				winningBoard[i][j].set(false);
			}
		}
		turn.set(Owner.FIRST);
		winner.set(Owner.NONE);
	}

	public ObjectProperty<Owner> turnProperty() {
		return turn;
	}

	public ObjectProperty<Owner> getSquare(int row, int column) {
		return board[row][column];
	}

	public BooleanProperty getWinningSquare(int row, int column) {
		return winningBoard[row][column];
	}

	public StringBinding getEndOfGameMessage() {
		return Bindings.createStringBinding(() -> {
            if (winner.get() == Owner.NONE) {
                return "Match nul!";
            } else {
                return "Game over. Le gagnant est " + winner.get().toString();
            }
        }, winner);
    }

	public void setWinner(Owner winner) {
		this.winner.set(winner);
	}

	public boolean validSquare(int row, int column) {
		return row >= 0 && row < BOARD_WIDTH && column >= 0 && column < BOARD_HEIGHT;
	}

	public void nextPlayer() {
		turn.set(turn.get().opposite());
	}

	public void play(int row, int column) {
		if (legalMove(row, column).get()) {
			board[row][column].set(turn.get());
			isWinner();
			nextPlayer();
		}
	}

	public BooleanBinding legalMove(int row, int column) {
		isWinner();
		return board[row][column].isEqualTo(Owner.NONE).and(gameOver().not());

	}

	private void isWinner() {
		boolean hasWinner = false;

		for (int row = 0; row < BOARD_HEIGHT; row++) {
			if (winLine(row, 0, 0, 1)) {
				hasWinner = true;
			}
		}

		for (int col = 0; col < BOARD_WIDTH; col++) {
			if (winLine(0, col, 1, 0)) {
				hasWinner = true;
			}
		}

		if (winLine(0, 0, 1, 1)) {
			hasWinner = true;
		}
		if (winLine(0, BOARD_WIDTH - 1, 1, -1)) {
			hasWinner = true;
		}

		if (!hasWinner && isBoardFull().get()) {
			setWinner(Owner.NONE);
		}
	}

	public NumberExpression getScore(Owner owner) {
	    IntegerBinding scoreBinding = Bindings.createIntegerBinding(() -> {
	        int count = 0;
	        for (int row = 0; row < BOARD_HEIGHT; row++) {
	            for (int col = 0; col < BOARD_WIDTH; col++) {
	                if (board[row][col].get() == owner) {
	                    count++;
	                }
	            }
	        }
	        return count;
	    }, board[0][0], board[0][1], board[0][2], // Lie toutes les cases
	         board[1][0], board[1][1], board[1][2],
	         board[2][0], board[2][1], board[2][2]);

	    return scoreBinding;
	}

	public BooleanBinding gameOver() {
		return winner.isNotEqualTo(Owner.NONE).or(isBoardFull());
	}

	/**
	 * Cette methode nous permet de voir les lignes (colonne, diagonale) gagnantes
	 * 
	 */

	private boolean winLine(int startRow, int startCol, int rowStep, int colStep) {
		Owner firstOwner = getSquare(startRow, startCol).get();
		if (firstOwner == Owner.NONE) {
			return false;
		}

		for (int i = 1; i < WINNING_COUNT; i++) {
			int row = startRow + i * rowStep;
			int col = startCol + i * colStep;
			if (!validSquare(row, col) || getSquare(row, col).get() != firstOwner) {
				return false;
			}
		}

		for (int i = 0; i < WINNING_COUNT; i++) {
			int row = startRow + i * rowStep;
			int col = startCol + i * colStep;
			winningBoard[row][col].set(true);
		}

		setWinner(firstOwner);
		return true;
	}

	private BooleanBinding isBoardFull() {
		BooleanProperty booleanBinding = new SimpleBooleanProperty(false);
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int col = 0; col < BOARD_WIDTH; col++) {
				if (board[row][col].get() == Owner.NONE) {
					booleanBinding.set(true);
				}
			}
		}
		return booleanBinding.not();
	}

	/**
	 * Retourne le gagnant du jeu.
	 *
	 * @return Le gagnant (X, O) ou NONE si pas de gagnant.
	 */
	public Owner getWinner() {
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			if (board[row][0].get() != Owner.NONE && board[row][0].get() == board[row][1].get()
					&& board[row][1].get() == board[row][2].get()) {
				return board[row][0].get();
			}
		}

		for (int col = 0; col < BOARD_WIDTH; col++) {
			if (board[0][col].get() != Owner.NONE && board[0][col].get() == board[1][col].get()
					&& board[1][col].get() == board[2][col].get()) {
				return board[0][col].get();
			}
		}
		if (board[0][0].get() != Owner.NONE && board[0][0].get() == board[1][1].get()
				&& board[1][1].get() == board[2][2].get()) {
			return board[0][0].get();
		}

		if (board[0][2].get() != Owner.NONE && board[0][2].get() == board[1][1].get()
				&& board[1][1].get() == board[2][0].get()) {
			return board[0][2].get();
		}

		return Owner.NONE;
	}
}