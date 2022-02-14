package no.ntnu.OS.sudokuApp.model;
/**
 * Represents a sudoku board that can be observed by other entities.
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public interface ObservableSudokuBoard {

    /**
     * Registers a new object as an observer.
     * @param sudokuBoardObserver the sudoku board observer to add.
     */
    void registerObserver(SudokuBoardObserver sudokuBoardObserver);

    /**
     * Removes an object as an observer.
     * @param sudokuBoardObserver
     */
    void removeObserver(SudokuBoardObserver sudokuBoardObserver);

    /**
     * Notifies the observers that the result is ready.
     * @param threadList the resulting threadlist.
     */
    void notifyObservers(ThreadList threadList);
}
