package konan.blakime.morpion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * @author BLACKIME CHRISTIANNA && KONAN EVRARD HYCKAEL
 */
public class TicTacToeSquare extends TextField {

	private static TicTacToeModel model = TicTacToeModel.getInstance();

	private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);
	private BooleanProperty winnerProperty = new SimpleBooleanProperty(false);

	private final int row;
	private final int column;

	public TicTacToeSquare(final int row, final int column) {
		this.row = row;
		this.column = column;
		this.setEditable(false);
		this.setFocusTraversable(false);
		ownerProperty.bind(model.getSquare(row, column));
		winnerProperty.bind(model.getWinningSquare(row, column));
		ownerProperty.addListener((obs, oldVal, newVal) -> updateText());

		winnerProperty.addListener((obs, oldVal, newVal) -> updateBackground());

		this.setOnMouseClicked(this::handleMouseClick);
		this.setOnMouseEntered(this::handleMouseEntered);
		this.setOnMouseExited(this::handleMouseExited);

		updateText();
		updateBackground();
	}

	public ObjectProperty<Owner> ownerProperty() {
		return ownerProperty;
	}

	public BooleanProperty colorProperty() {
		return winnerProperty;
	}

	public void updateText() {
		Owner owner = ownerProperty.get();
		if (owner == Owner.FIRST) {
			this.setText("X");
		} else if (owner == Owner.SECOND) {
			this.setText("O");
		} else {
			this.setText("");
		}
	}

	private void updateBackground() {
		if (winnerProperty.get()) {
			this.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-alignment: center;");
		} else {
			this.setStyle("-fx-background-color: white; -fx-font-size: 18; -fx-alignment: center;");
		}
	}

	private void handleMouseClick(MouseEvent event) {
		if (model.legalMove(row, column).get()) {
			model.play(row, column);
		}
	}

	private void handleMouseEntered(MouseEvent event) {
		if (!winnerProperty.get() && model.legalMove(row, column).get()) {
			this.setStyle("-fx-background-color: lightgreen; -fx-font-size: 18; -fx-alignment: center;");
		} else if (!winnerProperty.get()) {
			this.setStyle("-fx-background-color: red; -fx-font-size: 18; -fx-alignment: center;");
		} else if (winnerProperty.get()) {
			this.setStyle(
					"-fx-background-color: red; -fx-font-size: 26; -fx-font-weight: bold; -fx-alignment: center;");
		}
	}

	private void handleMouseExited(MouseEvent event) {
		updateBackground();
	}
}