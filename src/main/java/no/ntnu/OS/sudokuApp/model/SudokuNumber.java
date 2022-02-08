package no.ntnu.OS.sudokuApp.model;
/**
 * Represents a basic object that can hold a SudokuNumber's position and value.
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class SudokuNumber {

    private final int listID;

    private final int columnID;
    
    private final int number;

    private int failureRating;

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
        failureRating = 1;
    }

    /**
     * Makes the failure rating increase with one.
     */
    public void incrementFailureRating(){
        failureRating++;
    }

    /**
     * Gets the failure rating of this sudoku number.
     * @return the failure rating of this object.
     */
    public int getFailureRating(){
        return failureRating;
    }

    /**
     * Gets the listID of the SudokuNumber
     * @return ID of the list.
     */
    public int getListID() {
        return listID;
    }

    /**
     * Gets the columnID this object is in.
     * @return the column ID.
     */
    public int getColumnID() {
        return columnID;
    }

    /**
     * Gets the sudoku number that is in that cell.
     * @return the sudoku number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Checks if the position of two sudoku number matches and the number.
     * @param sudokuNumber the sudoku number to compare against.
     * @return <code>true</code> if the object and the input is at the same spot.
     *         <code>false</code> if the object and input is not at the same spot.
     */
    public boolean checkIfPositionIsSame(SudokuNumber sudokuNumber){
        checkIfObjectIsNull(sudokuNumber, "sudoku number");
        boolean valid = sudokuNumber.getColumnID() == this.columnID && sudokuNumber.getListID() == this.listID && sudokuNumber.getNumber() == this.number;
        return valid;
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
