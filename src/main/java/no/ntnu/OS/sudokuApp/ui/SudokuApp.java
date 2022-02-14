package no.ntnu.OS.sudokuApp.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.OS.sudokuApp.model.Row;
import no.ntnu.OS.sudokuApp.model.SudokuBoard;
import no.ntnu.OS.sudokuApp.ui.controllers.Controller;
import no.ntnu.OS.sudokuApp.ui.windows.SudokuWindow;
import no.ntnu.OS.sudokuApp.ui.windows.Window;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents the loading of the sudoku app.
 * @version 0.1
 * @author Group 13
 */
public class SudokuApp extends Application {

    private static SudokuApp sudokuApp;

    private SudokuBoard sudokuBoard;

    private Stage stage;

    private ExecutorService executorService;

    /**
      * Makes an instance of the SudokuApp class.
      */
    public SudokuApp(){
        try {
            executorService = Executors.newFixedThreadPool(4);
            sudokuApp = this;
        }catch (Exception exception){

        }
    }

    /**
     * Gets the sudoku app object.
     * @return the sudoku app.
     */
    public static SudokuApp getSudokuApp(){
        if (sudokuApp == null){
            synchronized (SudokuApp.class){
                sudokuApp = new SudokuApp();
            }
        }
        return sudokuApp;
    }

    /**
     * Makes a new sudoku board based on the input and returns the product.
     * @param sudokuSolution the sudoku solution to make into a board.
     * @return the new sudoku board.
     */
    public SudokuBoard makeAndGetNewSudokuBoardWithString(String sudokuSolution){
        checkString(sudokuSolution, "sudoku solution");
        this.sudokuBoard = new SudokuBoard(sudokuSolution);
        return sudokuBoard;
    }

    /**
     * Makes a new sudoku board with list of rows.
     * @param rowList the list with rows.
     * @return a new sudokuboard.
     */
    public SudokuBoard makeAndGetNewSudokuBoardWithListOfRows(List<Row> rowList){
        checkIfObjectIsNull(rowList, "row list");
        this.sudokuBoard = new SudokuBoard(rowList);
        return sudokuBoard;
    }

    /**
     * Gets the executor service. To do multithreading.
     * @return the executor service.
     */
    public ExecutorService getExecutorService(){
        return executorService;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        setNewScene(SudokuWindow.getSudokuWindow());
        stage.show();
    }

    /**
     * Changes the scene to a new scene.
     * @param window the window you want to change the scene to.
     * @throws IOException gets thrown if the scene could not be loaded.
     */
    public void setNewScene(Window window) throws IOException {
        checkIfObjectIsNull(window, "window");
        Controller controller = window.getController();
        checkIfObjectIsNull(controller, "controller");
        Scene scene = window.getScene();
        if (scene == null){
            String fileName = window.getFXMLName();
            checkString(fileName, "file name");
            String fullFilename = fileName + ".fxml";
            scene = loadScene(fullFilename , controller);
            window.setScene(scene);
        }
        String title = window.getTitleName();
        checkString(title, "title");
        String windowTitle = "Chat application 0.1v - " + title;
        controller.updateContent();
        stage.setTitle(windowTitle);
        stage.setScene(scene);
    }

    /**
     * Loads a scene and returns the scene after its loaded.
     * @param fullNameOfFile the full name of the FXML file you want to load with the .fxml part.
     * @param controller the controller you want this scene to have.
     * @return a scene that is loaded and ready to be displayed.
     * @throws IOException gets thrown if the scene is not loaded correctly or is missing.
     */
    private Scene loadScene(String fullNameOfFile, Controller controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fullNameOfFile));
        loader.setController(controller);
        return new Scene(loader.load());
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
}
