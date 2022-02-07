package no.ntnu.OS.sudokuApp.ui.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import no.ntnu.OS.sudokuApp.model.SudokuBoard;
import no.ntnu.OS.sudokuApp.model.SudokuNumber;
import no.ntnu.OS.sudokuApp.ui.SudokuApp;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class SudokuController implements Controller{

    @FXML
    private TableView<SudokuNumber> wrongNumbersTable;

    @FXML
    private TextField textField;

    @FXML
    private Button checkSolButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text errorText;

    private int spacing;

    /**
      * Makes an instance of the SudokuController class.
      */
    public SudokuController(){
        spacing = 20;
    }

    private void setButtonFunctions(){
        checkSolButton.setOnAction(event -> {
            String string = textField.getText();
            try {
                int dimensions = getDimensions(string);
                SudokuBoard sudokuBoard = SudokuApp.getSudokuApp().makeAndGetNewSudokuBoard(string);
                displaySudokuBoard(sudokuBoard, dimensions);
            }catch (NumberFormatException exception){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Could not solve");
                alert.setHeaderText("Could not solve sudoku puzzle");
                alert.setContentText("Could not solve sudoku puzzle since the format is not nxn. \nLike 4x4");
                alert.showAndWait();
            }
        });
    }

    private void addListeners(){
        String standardMessage = "Please enter a sudoku solution that is nxn big.";
        errorText.setText(standardMessage);
        checkSolButton.setDisable(true);
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            String text = newVal;
            String error = "";
            boolean disableButton = true;
            try {
                checkString(text, "new value");
                disableButton = false;
            }catch (IllegalArgumentException exception){
                error = "Solution cannot be empty.";
            }
            errorText.setText(error);
            checkSolButton.setDisable(disableButton);
        });
    }

    /**
     * Adds columns to the tableview.
     */
    private void addColumns(){
        TableColumn<SudokuNumber, Number> yCol = new TableColumn<>("Y pos");
        yCol.setCellValueFactory(sudoNumber -> new SimpleIntegerProperty(sudoNumber.getValue().getColumnID()));

        TableColumn<SudokuNumber, Number> xCol = new TableColumn<>("X pos");
        xCol.setCellValueFactory(sudoNum -> new SimpleIntegerProperty(sudoNum.getValue().getListID()));

        TableColumn<SudokuNumber, Number> wrongCol = new TableColumn<>("Wrong number");
        wrongCol.setCellValueFactory(sudoNum -> new SimpleIntegerProperty(sudoNum.getValue().getNumber()));

        wrongNumbersTable.getColumns().add(xCol);
        wrongNumbersTable.getColumns().add(yCol);
        wrongNumbersTable.getColumns().add(wrongCol);

        wrongNumbersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void displaySudokuBoard(SudokuBoard sudokuBoard, int dimensions){
        int realSize = dimensions + 1;
        GridPane sudokuGridPane = new GridPane();
        sudokuGridPane.setHgap(realSize);
        sudokuGridPane.setVgap(realSize);
        addDisplayNumbersOnTop(dimensions, sudokuGridPane);
        for (int xpos = 1; xpos <= dimensions; xpos++) {
            Iterator<Integer> it = sudokuBoard.getRowIterator(xpos);
            int ypos = 1;
            while (it.hasNext()) {
                int number = it.next();
                Label label = new Label(" " + number + " ");
                label.setId("" + ypos + "" + xpos);
                sudokuGridPane.add(label, ypos, xpos);
                ypos++;
            }
        }
        sudokuGridPane.styleProperty().set("-fx-alignment: center");
        scrollPane.setContent(sudokuGridPane);

        List<SudokuNumber> sudokuNumberList = sudokuBoard.checkPuzzleIsValid(SudokuApp.getSudokuApp().getExecutorService());

        wrongNumbersTable.setItems(FXCollections.observableArrayList(sudokuNumberList));

        sudokuNumberList.forEach(sudNum -> {
            sudokuGridPane.lookup("#" + sudNum.getColumnID() + "" + sudNum.getListID()).setStyle("-fx-background-color: red");
        });
    }



    private void addDisplayNumbersOnTop(int dimensions, GridPane gridPane){
        for (int i = 1; i <= dimensions; i++){
            String string = " " + i + " ";
            Label xLabel = new Label(string);
            Label yLabel = new Label(string);
            xLabel.styleProperty().set("-fx-border-style: hidden solid hidden hidden; -fx-border-width: 1; -fx-border-color: black;");
            yLabel.styleProperty().set("-fx-border-style: hidden hidden solid hidden; -fx-border-width: 1; -fx-border-color: black;");
            gridPane.add(xLabel, 0, i);
            gridPane.add(yLabel, i, 0);

        }
    }

    /**
     * Gets the dimensions of a string to check if its whole.
     * @param string the string to check for a nxn matrix.
     * @return the dimensions of this string. If string is 81 letters then it returns 9.
     * @throws NumberFormatException if the number is not an even square number.
     */
    private int getDimensions(String string){
        double dimensions = Math.sqrt(string.length());
        if (dimensions == (int) dimensions){
            return (int) dimensions;
        }else {
            throw new NumberFormatException("The input number must be a n x n matrix.");
        }
    }

    
    /**
     * Checks if a string is of a valid format or not.
     * @param stringToCheck the string you want to check.
     * @param errorPrefix the error the exception should have if the string is invalid.
     */
    private void checkString(String stringToCheck, String errorPrefix){
        checkIfObjectIsNull(stringToCheck, errorPrefix);
        if (stringToCheck.isEmpty()){
            throw new IllegalArgumentException("The " + errorPrefix + " cannot be empty.");
        }
    }
    
    /**
     * Checks if an object is null.
     * @param object the object you want to check.
     * @param error the error message the exception should have.
     */
    private void checkIfObjectIsNull(Object object, String error){
       if (object == null){
           throw new IllegalArgumentException("The " + error + " cannot be null.");
       }
    }

    @Override
    public void updateContent() {
        setButtonFunctions();
        addListeners();
        if (wrongNumbersTable.getColumns().isEmpty()){
            addColumns();
        }
    }

    @Override
    public void emptyContent() {
        textField.textProperty().set("");
        wrongNumbersTable = new TableView<>();
    }
}
