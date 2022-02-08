package no.ntnu.OS.sudokuApp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Represents an object that holds each cell we are going through.
 * @version 0.1
 * @author Steinar Hjelle Midthus
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

        // Takes in the numbers to go through
        while(it.hasNext() && !success){
            // Gets the next number to go through
            SudokuNumber sudoNumb = it.next();
            // Checks if number matches another number from the list.
            if (sudoNumb.getNumber() == number) {
                // return true if number matches. and goes out of the while-loop.
                success = true;
            }
        }
        
        //How it can be done.
        //sudokuNumberList.stream().anyMatch(sudnum -> sudnum.getNumber() == number);
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
        sudokuNumberList.remove(sudokuNumber);
    }

    /**
     * Removes all the numbers that are on the same row and column as the input number if it's not above 1.
     * @param sudokuNumber the sudoku number to remove and remove all copies of.
     */
    public void removeAllNumbersWithSameValueRowAndColumn(SudokuNumber sudokuNumber){
        checkIfSudokuNumberIsValid(sudokuNumber);
        removeSudokuNumber(sudokuNumber);
        List<SudokuNumber> rowList = getAllNumbersWithRowOrColumn(sudokuNumber, false);
        if (rowList.size() == 1){
            sudokuNumberList.remove(rowList.get(0));
        }
        List<SudokuNumber> columnList = getAllNumbersWithRowOrColumn(sudokuNumber, true);
        if (columnList.size() == 1){
            sudokuNumberList.remove(columnList.get(0));
        }
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
     * Prints all the elements in this thread list.
     */
    public void printAllElements(){
        sudokuNumberList.forEach(number -> System.out.print(number.getNumber() + " "));
        System.out.println();
    }

    /**
     * Gets the occurrence of this number in this row.
     * @param firstNumber the first number you want to get.
     * @return the sudokunumber object that represents this number and its position.
     */
    public SudokuNumber getFirstOfNumber(int firstNumber){
        checkIfNumberIsAboveZero(firstNumber, "first number");
        Optional<SudokuNumber> opSud = sudokuNumberList.stream().filter(sudnum -> sudnum.getNumber() == firstNumber).findFirst();
        //Todo: Endre denne slik at den kan kaste exceptions.
        return opSud.get();
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
