package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

public class Main extends Application {
    public void start(Stage stage) {

        //Creating main page
        BorderPane bPane = new BorderPane();
        bPane.setPadding(new Insets(10, 10, 10, 10));
        int prefWidth = 465; //For the generic 6x6 puzzle it seems to be the best width
        int prefHeight = 382; // For the generic 6x6 puzzle it seems to be the best height
        bPane.setPrefSize(prefWidth, prefHeight);

        //Creating buttons for redo, undo, clearing board , loading a puzzle from a file , loading a file from a text input and finally the show mistakes button
        Button redoButton = new Button("Redo");
        Button undoButton = new Button("Undo");
        Button clearButton = new Button("Clear the board");
        Button loadFileButton = new Button("Load a game from a file");
        Button loadTextButton = new Button("Load a game from text input");
        Button showMistakesButton = new Button("Show mistakes");

        //Create a grid.
        GridPane grid = new GridPane();
        //Making generic 6x6 puzzle
        Grid playingBoard = new Grid();

        class MakePlayBoard {
            public void reloadGrid(){
                grid.getChildren().clear();
                for (int i = 0; i < playingBoard.getSize(); i++) {
                    for (int j = 0; j < playingBoard.getSize(); j++) {
                        Cell cell;
                        cell = (playingBoard.getGridCells())[i][j];
                        TextField userInpField = (playingBoard.getGridCells())[i][j].getField();
                        userInpField.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue.matches("^[1-9]$")) //"^[1-9][0-9]{0,7}$"
                            {
                                newValue = newValue.replaceAll("^0", "");
                                newValue = newValue.replaceAll("[^\\d]", "");
                                if(newValue.length() > 0) {
                                    newValue = newValue.substring(0,1);
                                }
                                userInpField.setText(newValue);
                            }
                            if (newValue.length() > 0) {
                                cell.setCellValue(newValue);
                                cell.setText(newValue);
                                playingBoard.addModifiedCell(cell.getCellID());
                                if (undoButton.isDisabled()) {
                                    undoButton.setDisable(false);
                                    clearButton.setDisable(false);
                                }
                                if(playingBoard.getModifiedCells().size() == playingBoard.getSize()*playingBoard.getSize()) {
                                    if(showMistakesButton.isDisabled()) {
                                        showMistakesButton.setDisable(false);
                                    }
                                }
                            } else {
                                cell.setCellValue("0");
                                cell.setText("");
                                playingBoard.removeModifiedCell(cell.getCellID());
                                playingBoard.changeColour("normal");
                                if (playingBoard.getModifiedCells().size() == 0) {
                                    undoButton.setDisable(true);
                                    clearButton.setDisable(true);
                                }
                                if(playingBoard.getModifiedCells().size() < playingBoard.getSize()*playingBoard.getSize()) {
                                    showMistakesButton.setDisable(true);
                                }
                            }
                        });
                        grid.add((playingBoard.getGridCells())[i][j].returnNode(), j, i);
                    }
                }
            }
        }

        MakePlayBoard makePlayBoard = new MakePlayBoard();
        makePlayBoard.reloadGrid();

        bPane.setCenter(grid);
        bPane.setMargin(grid, new Insets(5, 5, 5, 5));

        //Layout for buttons
        FlowPane buttonPane = new FlowPane(Orientation.VERTICAL);
        buttonPane.getChildren().addAll(redoButton,undoButton,clearButton,loadFileButton,loadTextButton,showMistakesButton);
        bPane.setLeft(buttonPane);

        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setVgap(2);
        buttonPane.setHgap(2);

        //Creating scene
        Scene scene = new Scene(bPane);
        stage.setScene(scene);
        stage.setTitle("KenKen");
        stage.show();

        //Event handler for undo button
        undoButton.addEventHandler(ActionEvent.ANY, e -> {
            try {
                playingBoard.undo();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        });

        //Event handler for redo button
        redoButton.addEventHandler(ActionEvent.ANY, e -> {
            playingBoard.redo();
        });

        //Event handler for clear button
        clearButton.addEventHandler(ActionEvent.ANY, e -> {
            Alert clearAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to clear the board?");
            clearAlert.setTitle("Alert: Clearing Board");

            clearAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    playingBoard.clearGrid();
                }
            });
        });

        //Event handler for loading the file from the file explorer
        loadFileButton.addEventHandler(ActionEvent.ANY, e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose the puzzle file you would like to load");
            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("txt files",
                    "*.txt");
            fileChooser.getExtensionFilters().add(txtFilter);
            File desiredFile = fileChooser.showOpenDialog(stage);
            
            if (desiredFile != null && desiredFile.exists() && desiredFile.canRead()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(desiredFile));
                    String currentLine;
                    String puzzleCages = "";
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        puzzleCages += (currentLine + "\n");
                    }
                    if(playingBoard.isFormatCorrect(puzzleCages)) {
                        playingBoard.readFromString(puzzleCages);
                        makePlayBoard.reloadGrid();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Please check how the file is formatted please");
                        Optional<ButtonType> result = alert.showAndWait();
                    }
                    bufferedReader.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //Event Handler for undo button
        loadTextButton.addEventHandler(ActionEvent.ANY, e -> {
            TextArea userInpArea = new TextArea();

            Button loadButton = new Button("Load");
            loadButton.setPrefWidth(70);

            Button cancelButton = new Button("Cancel");
            cancelButton.setPrefWidth(70);

            HBox buttonBox = new HBox();
            BorderPane loadTextDiaPane = new BorderPane();

            buttonBox.getChildren().addAll(loadButton, cancelButton);
            loadTextDiaPane.setBottom(buttonBox);
            loadTextDiaPane.setCenter(userInpArea);

            Stage loadTextDiaStage = new Stage();
            loadTextDiaStage.setTitle("Load from a file using text input");
            loadTextDiaStage.setScene(new Scene(loadTextDiaPane));
            loadTextDiaStage.show();

            //Event Handler for load button
            loadButton.addEventHandler(ActionEvent.ANY, ex -> {
                if(playingBoard.isFormatCorrect(userInpArea.getText())) {
                    playingBoard.readFromString(userInpArea.getText());
                    makePlayBoard.reloadGrid();
                    loadTextDiaStage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Provided data is invalid!");
                    Optional<ButtonType> result = alert.showAndWait();
                }
            });

            //Event handler for the cancel button when loading the file from a text input
            cancelButton.addEventHandler(ActionEvent.ANY, ex -> {
                Alert cancelAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you don't want to load the file?");

                cancelAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        loadTextDiaStage.close();
                    }
                });
            });
        });

        //Event handler for show mistakes button
        showMistakesButton.addEventHandler(ActionEvent.ANY, e -> {playingBoard.isSolved(); playingBoard.changeColour("highlight");});
    }

    public static void main(String[] args) {
        launch(args);
    }
}
