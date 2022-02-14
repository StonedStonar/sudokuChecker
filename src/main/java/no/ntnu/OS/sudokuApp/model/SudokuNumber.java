package no.ntnu.OS.sudokuApp.model;
/**
 * Represents a basic object that can hold a SudokuNumber's position and value.
 * @version 0.1
 * @author Group 13
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
     * Checks if this sudoku number is in the same cell.
     * @param startX the start position of the cell. (included)
     * @param endX the end position of the cell (included)
     * @param startY the start y position of the cell (included)
     * @param endY the end position of the cell (included)
     * @return
     */
    public boolean checkIfNumberIsInCell(int startX, int endX, int startY, int endY){
        return getListID() >= startX && getListID() <= endX && getColumnID() >= startY && getColumnID() <= endY && getNumber() == getNumber();
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
