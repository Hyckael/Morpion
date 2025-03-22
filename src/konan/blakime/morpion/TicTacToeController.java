package konan.blakime.morpion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author BLACKIME CHRISTIANNA && KONAN EVRARD HYCKAEL
 */

public class TicTacToeController {

	private int xCount = 0; // Compteur pour X
	private int oCount = 0; // Compteur pour O
	private int caseLibre = 0;

	@FXML
	private GridPane gridPane;

	@FXML
	private Label xCountLabel, oCountLabel, freeCountLabel;
	@FXML

	private Button restartButton;

	@FXML
	private Label endOfGameLabel;

	private TicTacToeModel model = TicTacToeModel.getInstance();

	/**
	 * Initialise le contrôleur.
	 */
	@FXML
	public void initialize() {

		endOfGameLabel.textProperty().bind(model.getEndOfGameMessage());
		endOfGameLabel.visibleProperty().bind(model.gameOver());

		// Création et ajout les TicTacToeSquare à la grille
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				TicTacToeSquare square = new TicTacToeSquare(row, col);
				square.setPrefSize(80, 80);
				gridPane.add(square, col, row);
				model.getSquare(row, col).addListener((obs, oldVal, newVal) -> {
					updateCounts();
				});
			}
		}
		bindButtonsToModel();
		updateCounts();
	}

	/**
	 * Lie les boutons du plateau de jeu au modèle.
	 */
	private void bindButtonsToModel() {

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				TicTacToeSquare square = (TicTacToeSquare) gridPane.getChildren().get(row * 3 + col);

				// Mise à jour du texte du bouton en fonction du modèle
				model.getSquare(row, col).addListener((obs, oldVal, newVal) -> {
					square.updateText();
				});

				// Désactiver le bouton si la case est déjà occupée ou si le jeu est terminé
				model.legalMove(row, col).addListener((obs, oldVal, newVal) -> {
					square.setDisable(!newVal);
				});
			}
		}
	}

	/**
	 * Gère le clic sur un bouton du plateau de jeu.
	 */
	@FXML
	private void handleButtonClick(javafx.event.ActionEvent event) {
		Button button = (Button) event.getSource();
		Integer row = GridPane.getRowIndex(button);
		Integer col = GridPane.getColumnIndex(button);

		if (row != null && col != null) {
			model.play(row, col);
			updateCounts();
		}
	}

	/**
	 * Met à jour les labels pour afficher le nombre de cases occupées par X et O.
	 */
	private void updateCounts() {
		xCount = model.getScore(Owner.FIRST).intValue(); // Nombre de cases pour X
		oCount = model.getScore(Owner.SECOND).intValue(); // Nombre de cases pour O
		caseLibre = 9 - (xCount + oCount);

		xCountLabel.setText(xCount + " case pour X");
		oCountLabel.setText(oCount + " case pour 0");
		freeCountLabel.setText(caseLibre + " cases libres");

		freeCountLabel.visibleProperty().bind(model.gameOver().not());
		endOfGameLabel.visibleProperty().bind(model.gameOver());

		if (model.gameOver().get()) {
			xCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: red;");
			oCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: red;");
		} else if (model.turnProperty().get() == Owner.FIRST) {
			xCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: cyan;");
			oCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: red;");
		} else {
			xCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: red;");
			oCountLabel.setStyle("-fx-font-size: 18; -fx-background-color: cyan;");
		}

	}

	/**
	 * Gère bouton "Restart".
	 */
	@FXML
	private void handleRestart() {
		model.restart();
		updateCounts();
	}

}
