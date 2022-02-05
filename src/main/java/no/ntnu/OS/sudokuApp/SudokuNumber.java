package no.ntnu.OS.sudokuApp;
/**
 *
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class SudokuNumber {

    private int listID;

    private int columnID;
    
    private int number;

    /**
     * Makes a basic instance of the SudokuNumber
     * @param listID the ID of the list this sudoku number is in. Y direction.
     * @param columnID the column ID this number is in. Y direction. This is the index in the list.
     * @param number the number you want to be the main focus on.
     */
    public SudokuNumber(int listID, int columnID, int number){
        checkIfNumberIsAboveZero(listID, "listID");
        checkIfNumberIsAboveZero(columnID, "columnID");
        checkIfNumberIsAboveZero(number, "number");
        this.listID = listID;
        this.columnID = columnID;
        this.number = number;
    }

    /**
     * 
     * @return
     */
    public int getListID() {
        return listID;
    }

    /**
     * 
     * @return
     */
    public int getColumnID() {
        return columnID;
    }

    /**
     * 
     * @return
     */
    public int getNumber() {
        return number;
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
