package no.ntnu.OS.sudokuApp.ui.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import no.ntnu.OS.sudokuApp.model.Row;
import no.ntnu.OS.sudokuApp.model.SudokuBoard;
import no.ntnu.OS.sudokuApp.model.SudokuBoardObserver;
import no.ntnu.OS.sudokuApp.model.SudokuNumber;
import no.ntnu.OS.sudokuApp.ui.SudokuApp;
import no.ntnu.OS.sudokuApp.ui.popups.AlertBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Represents a sudoku controller that helps the sudoku window with actions on the page.
 * @version 0.1
 * @author Group 13
 */
public class SudokuController implements Controller, SudokuBoardObserver {

    @FXML
    private TableView<SudokuNumber> wrongNumbersTable;

    @FXML
    private TextField textField;

    @FXML
    private Button checkSolButton;

    @FXML
    private Button importSudokuSoltuionButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text errorText;

    @FXML
    private CheckBox delayBox;

    private GridPane sudokuGridPane;

    /**
      * Makes an instance of the SudokuController class.
      */
    public SudokuController() {
        //Lul
    }

    /**
     * Sets the functions of the buttons.
     */
    private void setButtonFunctions(){
        checkSolButton.setOnAction(event -> {
            String string = textField.getText();
            try {
                displaySudokuBoard(SudokuApp.getSudokuApp().makeAndGetNewSudokuBoardWithString(string));
                textField.setText("");
            }catch (NumberFormatException exception){
                makeAndShowCouldNotSolveAlert();
            }catch (IllegalArgumentException exception){
                Alert alert = new AlertBuilder(Alert.AlertType.ERROR).setTitle("The input cannot be empty")
                        .setHeaderText("The input cannot be empty").setContext("The input sudoku solution cannot be empty. \nPlease try again").build();
                alert.showAndWait();
            }
        });
        importSudokuSoltuionButton.setOnAction(event -> chooseAndLoadFile());
    }

    /**
     * Makes and shows the error message.
     */
    private void makeAndShowCouldNotSolveAlert(){
        Alert alert = new AlertBuilder(Alert.AlertType.ERROR).setTitle("Could not solve")
                .setHeaderText("Could not solve sudoku puzzle").setContext("Could not solve sudoku puzzle since the format is not nxn. \nLike 4x4. Remember it can only contain numbers. \nPlease try again.").build();
        alert.showAndWait();
    }

    /**
     * Chooses a file and loads the sudoku board.
     */
    private void chooseAndLoadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        List<Row> rowList = new LinkedList<>();
        if (file != null && file.isFile()){
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
                String word = bufferedReader.readLine();
                while (word != null){
                    Row row = new Row();
                    String[] letters = word.split(",");
                    Arrays.stream(letters).forEach(letter -> row.addNumber(Integer.parseInt(letter)));
                    rowList.add(row);
                    word = bufferedReader.readLine();
                }
                displaySudokuBoard(SudokuApp.getSudokuApp().makeAndGetNewSudokuBoardWithListOfRows(rowList));
            } catch (IOException exception) {
                Alert alert = new AlertBuilder(Alert.AlertType.ERROR).setTitle("File is empty")
                        .setHeaderText("File is empty").setContext("The chosen file is empty. \nPlease choose a .csv file.").build();
                alert.showAndWait();
            }catch (IllegalArgumentException exception){
                makeAndShowCouldNotSolveAlert();
            }
        }
    }

    /**
     * Adds all the defined listeners.
     */
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

        TableColumn<SudokuNumber, String> validNumber = new TableColumn<>("Valid");
        validNumber.setCellValueFactory(sudoNum -> {
            String valid = "true";
            if (sudoNum.getValue().isInvalidNumber()){
                valid = "false";
            }
            return new SimpleStringProperty(valid);
        });

        String blue = "blue";
        String red = "red";
        String green = "green";
        String gold = "gold";

        TableColumn<SudokuNumber, String> colorCol = new TableColumn<>("Error color");
        colorCol.setCellValueFactory(sudoNum -> {
            String color;
            if (!sudoNum.getValue().isInvalidNumber()){
                color = switch (sudoNum.getValue().getFailureRating()) {
                    case 2 -> blue;
                    case 3 -> red;
                    default -> green;
                };
            }else {
                color = gold;
            }
            return new SimpleStringProperty(color);
        });

        wrongNumbersTable.getColumns().add(xCol);
        wrongNumbersTable.getColumns().add(yCol);
        wrongNumbersTable.getColumns().add(wrongCol);
        wrongNumbersTable.getColumns().add(colorCol);
        wrongNumbersTable.getColumns().add(validNumber);
    }

    /**
     * Makes the sudoku board and displays it for the user. Also starts the checks in the background.
     */
    private void displaySudokuBoard(SudokuBoard sudokuBoard){
        int dimensions = sudokuBoard.getSize();
        checkSolButton.setDisable(true);
        importSudokuSoltuionButton.setDisable(true);
        int realSize = dimensions + 1;
        sudokuGridPane = new GridPane();
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

        ExecutorService executorService = SudokuApp.getSudokuApp().getExecutorService();

        sudokuBoard.registerObserver(this);

        executorService.submit(() -> sudokuBoard.checkPuzzleIsValid(SudokuApp.getSudokuApp().getExecutorService(), delayBox.isSelected()));
    }

    /**
     * Displays the invalid numbers that is given by the underlying threads.
     * @param sudokuNumberList the sudoku number list to check.
     */
    private void displayInvalidNumbers(List<SudokuNumber> sudokuNumberList){
        String blue = "deepskyblue";
        String red = "red";
        String green = "darkseagreen";
        String gold = "gold";
        sudokuNumberList.forEach(sudNum -> {
            String color;
            if (!sudNum.isInvalidNumber()){
                color = switch (sudNum.getFailureRating()) {
                    case 2 -> blue;
                    case 3 -> red;
                    default -> green;
                };
            }else {
                color = gold;
            }

            sudokuGridPane.lookup("#" + sudNum.getColumnID() + "" + sudNum.getListID()).setStyle("-fx-background-color: " + color + ";");
        });
        wrongNumbersTable.setItems(FXCollections.observableArrayList(sudokuNumberList));
        importSudokuSoltuionButton.setDisable(false);
    }


    /**
     * Adds display numbers on the side and on the top.
     * @param dimensions the dimensions of the board.
     * @param gridPane the grid pane to add the numbers to.
     */
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

    @Override
    public void update(List<SudokuNumber> sudokuNumberList) {
        Platform.runLater(() -> {
            displayInvalidNumbers(sudokuNumberList);
            Alert alert = new AlertBuilder(Alert.AlertType.INFORMATION).setTitle("Checks are done").setHeaderText("Checks are done")
                    .setContext("The row, cell and column checks are now done.").build();
            alert.showAndWait();
        });

    }

    @Override
    public void indicateError() {
        Platform.runLater(() -> {
            Alert alert = new AlertBuilder(Alert.AlertType.INFORMATION).setTitle("Error processing").setHeaderText("Error processing")
                    .setContext("A error has occurred processing the input sudoku board. \nCheck the format and please try again.").build();
            alert.showAndWait();
        });
    }
}
