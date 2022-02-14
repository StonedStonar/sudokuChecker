package no.ntnu.OS.sudokuApp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Represents an object that holds each cell we are going through.
 * @version 0.1
 * @author Group 13
 */
public class ThreadList {

    private final List<SudokuNumber> sudokuNumberList;

    /**
      * Makes an instance of the ThreadList class.
      */
    public ThreadList(){
        sudokuNumberList = new ArrayList<>();
    }

    /**
     * Add a sudoku number to the list.
     * @param sudokuNumber add number to list.
     */
    public void addSudokuNumber(SudokuNumber sudokuNumber){
        checkIfSudokuNumberIsValid(sudokuNumber);
        sudokuNumberList.add(sudokuNumber);
    }

    /**
     * Checks if a number exist in a list or not.
     * @param number to be checked against other numbers in a list.
     * @return (true) if the number matches any number in the threadList,
     * (false) if the number is not in the list.
     */
    public boolean contains(int number){

        checkIfNumberIsAboveZero(number, "number");
        Iterator<SudokuNumber> it = this.sudokuNumberList.iterator();
        boolean success = false;

        while(it.hasNext() && !success){
            SudokuNumber sudoNumb = it.next();
            if (sudoNumb.getNumber() == number) {
                success = true;
            }
        }

        return success;
    }

    /**
     * Checks if the input sudoku number has the same exact position as one in the list.
     * @param sudokuNumber the sudoku number to check.
     * @return <code>true</code> if a sudoku number in this list matches the input's position and value.
     *         <code>false</code> if a sudoku number in this list does not match completely.
     */
    public boolean containsSudokuNumber(SudokuNumber sudokuNumber){
        checkIfSudokuNumberIsValid(sudokuNumber);
        boolean valid = sudokuNumberList.stream().anyMatch(num -> num.checkIfPositionIsSame(sudokuNumber));
        return valid;
    }

    /**
     * Removes a sudoku number from the list.
     * @param sudokuNumber the sudoku number to remove.
     */
    private void removeSudokuNumber(SudokuNumber sudokuNumber){
        List<SudokuNumber> sudokuNumbers = sudokuNumberList.stream().filter(sudNum -> sudNum.checkIfPositionIsSame(sudokuNumber)).toList();
        sudokuNumberList.removeAll(sudokuNumbers);
    }

    /**
     * Removes all the numbers that are on the same row and column as the input number if it's not above 1.
     * @param sudokuNumbers the sudoku numbers to remove and remove all copies of.
     * @param sudokuBoard the sudoku board it's about.
     * @param cell <code>true</code> if the list is a cell.
     *             <code>false</code> if the list is not a cell.
     */
    public void removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(List<SudokuNumber> sudokuNumbers, SudokuBoard sudokuBoard, boolean cell){
        checkIfSudokuNumberListIsValid(sudokuNumbers);
        sudokuNumbers.forEach(sudNum -> removeSudokuNumber(sudNum));
        sudokuNumbers.forEach(sudokuNumber -> {
            if (cell){
                removeAllNumbersInTheSameCell(sudokuNumber, sudokuBoard);
            }else {
                boolean rowRemoved = removeRowNumber(sudokuNumber);
                boolean columnRemoved = removeColumnNumber(sudokuNumber);

                if (!rowRemoved && columnRemoved){
                    removeRowNumber(sudokuNumber);
                }
            }
        });
    }

    /**
     * Removes a sudoku row number if its only one of them.
     * @param sudokuNumber the sudoku number to remove.
     * @return <code>true</code> if the object is removed.
     *         <code>false</code> if the object is not removed.
     */
    private boolean removeRowNumber(SudokuNumber sudokuNumber){
        boolean valid = false;
        List<SudokuNumber> rowList = getAllNumbersWithRowOrColumn(sudokuNumber, false);
        if (rowList.size() == 1){
            sudokuNumberList.remove(rowList.get(0));
            valid = true;
        }
        return valid;
    }

    /**
     * Removes a sudoku column number if its only one of them.
     * @param sudokuNumber the sudoku number to remove.
     * @return <code>true</code> if the object is removed.
     *         <code>false</code> if the object is not removed.
     */
    private boolean removeColumnNumber(SudokuNumber sudokuNumber){
        boolean valid = false;
        List<SudokuNumber> columnList = getAllNumbersWithRowOrColumn(sudokuNumber, true);
        if (columnList.size() == 1){
            sudokuNumberList.remove(columnList.get(0));
            valid = true;
        }
        return valid;
    }

    /**
     * Removes all the numbers that are in the same cell as the sudoku number if amount is not above 2.
     * @param sudokuNumber the sudoku number to remove duplicates for.
     * @param sudokuBoard the current sudoku board.
     * @return
     */
    private boolean removeAllNumbersInTheSameCell(SudokuNumber sudokuNumber, SudokuBoard sudokuBoard){
        boolean valid = false;
        List<SudokuNumber> sudokuNumbers = getAllNumbersWithinTheSameCell(sudokuNumber, sudokuBoard);
        if (sudokuNumbers.size() == 1){
            sudokuNumberList.remove(sudokuNumbers.get(0));
            valid = true;
        }
        return valid;
    }

    /**
     * Gets all the number that is on the same row or column.
     * @param sudokuNumber the sudoku number to search for.
     * @param isColumn <code>true</code> if it's going to look for numbers on the same column.
     *                 <code>false</code> if it's going to look for numbers on the same row.
     * @return a list with all the numbers on the same row/column.
     */
    private List<SudokuNumber> getAllNumbersWithRowOrColumn(SudokuNumber sudokuNumber, boolean isColumn){
        checkIfSudokuNumberIsValid(sudokuNumber);
        return sudokuNumberList.stream().filter(sudNum -> sudNum.getNumber() == sudokuNumber.getNumber() && ((sudNum.getListID() == sudokuNumber.getListID() && !isColumn) || (sudNum.getColumnID() == sudokuNumber.getColumnID() && isColumn))).toList();
    }

    /**
     * Gets all the numbers that are in the same cell. Done by magic and math.
     * @param sudokuNumber the sudoku number that is in a cell.
     * @param sudokuBoard the sudoku board that has the size.
     * @return a list with all the numbers that is equal to the input sudoku number in value and placement by cell.
     */
    private List<SudokuNumber> getAllNumbersWithinTheSameCell(SudokuNumber sudokuNumber, SudokuBoard sudokuBoard){
        double dimensions = Math.sqrt(sudokuBoard.getSize());
        List<SudokuNumber> sudNumList = new ArrayList<>();
        if (dimensions == (int) dimensions){
            int startX = 0;
            int endX = 0;
            int startY = 0;
            int endY = 0;
            int i = 0;
            while (checkIfOneOfTwoNumbersAreZero(startX, startY) && i < dimensions){
                int startVal = (int) (dimensions * 0) + 1;
                int stopVal = (int) dimensions * (i+1);
                if (sudokuNumber.getColumnID() >= startVal && sudokuNumber.getColumnID() <= stopVal){
                    startX = startVal;
                    endX = stopVal;
                }
                if (sudokuNumber.getListID() >= startVal && sudokuNumber.getListID() <= stopVal){
                    startY = startVal;
                    endY = stopVal;
                }
                i++;
            }
            Iterator<SudokuNumber> it = sudokuNumberList.iterator();
            int finalEndX = endX;
            int finalStartX = startX;
            int finalStartY = startY;
            int finalEndY = endY;
            it.forEachRemaining(num -> {
                if (num.checkIfNumberIsInCell(finalStartX, finalEndX, finalStartY, finalEndY)){
                    sudNumList.add(num);
                }
            });
        }else {
            throw new NumberFormatException("The dimensions of the Sudoku board must be nxn.");
        }
        return sudNumList;
    }

    /**
     * Checks if one of the two numbers are zero.
     * @param val1 the first value.
     * @param val2 the last value.
     * @return <code>true</code> if one of the values are zero.
     *         <code>false</code> if none of the values are zero.
     */
    private boolean checkIfOneOfTwoNumbersAreZero(int val1, int val2){
        return val1 == 0 || val2 == 0;
    }

    /**
     * Gets the occurrence of this number in this row.
     * @param firstNumber the first number you want to get.
     * @return the sudokunumber object that represents this number and its position.
     */
    public SudokuNumber getFirstOfNumber(int firstNumber){
        checkIfNumberIsAboveZero(firstNumber, "first number");
        Optional<SudokuNumber> opSud = sudokuNumberList.stream().filter(sudnum -> sudnum.getNumber() == firstNumber).findFirst();
        if (opSud.isPresent()){
            return opSud.get();
        }else {
            throw new IllegalArgumentException("The number with the value " + firstNumber + " is not in this threadlist.");
        }
    }

    /**
     * Gets all the sudoku numbers.
     * @return all the sudoku numbers.
     */
    public List<SudokuNumber> getAllSudokuNumbers(){
        return this.sudokuNumberList;
    }

    /**
     * Checks if the sudoku number is valid.
     * @param sudokuNumber the number to check.
     */
    private void checkIfSudokuNumberIsValid(SudokuNumber sudokuNumber){
        checkIfObjectIsNull(sudokuNumber, "Sudoku number");
    }

    /**
     * Checks if the sudoku number list is zero or not.
     * @param sudokuNumbers the list with all the sudoku numbers.
     */
    private void checkIfSudokuNumberListIsValid(List<SudokuNumber> sudokuNumbers){
        checkIfObjectIsNull(sudokuNumbers, "sudoku numbers");
    }

    /**
     * Checks if the number is above zero.
     * @param numberToCheck the number to check.
     * @param prefix the error that the exception should have.
     */
    private void checkIfNumberIsAboveZero(int numberToCheck, String prefix){
        if (numberToCheck < 0){
            throw new IllegalArgumentException("The " + prefix + " must be above 0.");
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
