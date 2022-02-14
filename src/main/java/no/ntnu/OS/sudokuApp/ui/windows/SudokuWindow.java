package no.ntnu.OS.sudokuApp.ui.windows;

import javafx.scene.Scene;
import no.ntnu.OS.sudokuApp.ui.controllers.Controller;
import no.ntnu.OS.sudokuApp.ui.controllers.SudokuController;

/**
 * Represents the main window of the sudoku application.
 * @version 0.1
 * @author Group 13
 */
public class SudokuWindow implements Window{

    private final String title;

    private final SudokuController controller;

    private Scene scene;

    private final String fxmlName;

    private static SudokuWindow sudokuWindow;

    /**
      * Makes an instance of the SudokuWindow class.
      */
    private SudokuWindow(){
        this.title = "Sudoku";
        this.fxmlName = "SudokuWindow";
        this.controller = new SudokuController();
    }

    /**
     * Gets the sudoku window.
     * @return the sudoku window.
     */
    public static SudokuWindow getSudokuWindow() {
        if (sudokuWindow == null){
            synchronized (SudokuWindow.class){
                sudokuWindow = new SudokuWindow();
            }
        }
        return sudokuWindow;
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
    public Controller getController() {
        return controller;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public String getFXMLName() {
        return fxmlName;
    }

    @Override
    public String getTitleName() {
        return title;
    }

    @Override
    public void setScene(Scene scene) {
        checkIfObjectIsNull(scene, "scene");
        this.scene = scene;
    }
}
