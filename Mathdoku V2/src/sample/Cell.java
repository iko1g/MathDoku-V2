package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Cell implements Cloneable{

    VBox vbox = new VBox();
    private Label operationNumberLabel;
    TextField userInpField = new TextField();
    private int cellId;
    private int cellValue = 0;
    private Color cellColour;
    private Cage cage;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Cell cloned = (Cell) super.clone();
        cloned.setCellValue(String.valueOf(cellValue));
        cloned.userInpField = new TextField(this.userInpField.getText());
        cloned.setCellId(this.cellId);
        cloned.cage = (this.cage);
        return cloned;
    }

    public Cell(int cellId, Color cellColour, Cage cage) {
        this.operationNumberLabel = new Label("");
        this.cellId = cellId;
        this.cellColour = cellColour;
        this.cage = cage;
    }

    public Cell(Label operationNumberLabel, int cellId, Color cellColour, Cage cage) {
        this.operationNumberLabel = operationNumberLabel;
        this.cellId = cellId;
        this.cellColour = cellColour;
        this.cage = cage;
    }
    public void setCellId(int id) {
        this.cellId = id;
    }

    public int getCellID() {
        return cellId;
    }

    public TextField getField() {
        return userInpField;
    }

    public void setText(String s) {
        userInpField.setText(s);
    }

    public String getText() {
        return userInpField.getText();
    }

    public void setCellValue(String s){
        this.cellValue = Integer.parseInt(s);
    }

    public int getCellValue() {
        return cellValue;
    }

    public Label operationNumberLabel() {
        return operationNumberLabel;
    }

    public void highlight(String s) {
        if(s.equals("highlight")) {
            userInpField.setStyle("-fx-background-color: red; overflow:hidden; -fx-background-insets: 1 1 1 1");
            vbox.setStyle("-fx-background-color: red; overflow:hidden; -fx-background-insets: 1 1 1 1");
        } else if(s.equals("normal")) {
            userInpField.setStyle("-fx-background-color: white; overflow:hidden; -fx-background-insets: 1 1 1 1");
            vbox.setStyle("-fx-background-color: white; overflow:hidden; -fx-background-insets: 1 1 1 1");
        } else if(s.equals("win")) {
            userInpField.setStyle("-fx-background-color: green; overflow:hidden; -fx-background-insets: 1 1 1 1");
            vbox.setStyle("-fx-background-color: green; overflow:hidden; -fx-background-insets: 1 1 1 1");
        }

    }
    public VBox returnNode() {
        vbox.setBorder(new Border(new BorderStroke(cellColour, cellColour, cellColour, cellColour,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(5), new Insets(1, 1, 1, 1))));
        GridPane.setVgrow(userInpField, Priority.ALWAYS);
        userInpField.setPrefColumnCount(100);
        vbox.getChildren().addAll(operationNumberLabel, userInpField);
        operationNumberLabel.setAlignment(Pos.CENTER);
        highlight("normal");
        return vbox;
    }
}