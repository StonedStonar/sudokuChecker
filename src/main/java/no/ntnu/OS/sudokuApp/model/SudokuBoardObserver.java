package no.ntnu.OS.sudokuApp.model;

import java.util.List;

/**
 * Represents objects that want to observe a sudoku board.
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public interface SudokuBoardObserver {

    /**
     * Updates the observer with new data.
     * @param sudokuNumberList the list with all the errors.
     */
    void update(List<SudokuNumber> sudokuNumberList);

}
