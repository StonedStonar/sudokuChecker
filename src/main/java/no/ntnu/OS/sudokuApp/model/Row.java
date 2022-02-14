package no.ntnu.OS.sudokuApp.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a class that holds a row and numbers in it.
 * @version 0.1
 * @author Group 13
 */
public class Row {

    private final ArrayList<Integer> rowList;

    private int size;

    private boolean predefinedSize;



    /**
     * Makes an instance of the row class.
     * @param size the max size this row should have. This is in the X direction.
     */
    public Row(int size){
        checkIfNumberIsAboveZero(size, "size");
        rowList = new ArrayList<>(size);
        this.size = size;
        predefinedSize = true;
    }

    /**
     * Makes an instance of row where the size is not set.
     */
    public Row(){
        predefinedSize = false;
        rowList = new ArrayList<>();
        this.size = 0;
    }

    /**
     * Adds a number to the row.
     * @param numberToAdd the number you want to add to the row.
     */
    public void addNumber(int numberToAdd){
        checkNumberToAdd(numberToAdd);
        rowList.add(numberToAdd);
        if (!predefinedSize){
            size++;
        }
    }

    /**
     * Returns an iterator over this row.
     * @return an iterator for this row.
     */
    public Iterator<Integer> getIterator(){
        return rowList.iterator();
    }

    /**
     * Get the size of numbers given for the row.
     * @return max size of the row.
     */
    public int getSizeRow() {
         return size;
    }

    /**
     * Checks if number is between max and min.
     * @param number the number to be validated.
     * @param min the minimum number value. The minimum value is included.
     * @param max maximum number value. The maximum value is included.
     */
    private void checkIfNumberIsValid(int number, int min, int max, String prefix) {
        if (number > max || number < min) {
            throw new IllegalArgumentException("Invalid input! " + prefix + " can't be above "
                    + max  + " or under " + min + "to be valid");
        }
    }

    /**
     * Checks if the number to add is above zero.
     * @param numberToAdd the number you want to add.
     *
     */
    private void checkNumberToAdd(int numberToAdd){
        checkIfNumberIsValid(numberToAdd, 0, Integer.MAX_VALUE, "number to add");
    }

    /**
     * Checks if the number is above zero.
     * @param numberToCheck the number to check.
     * @param prefix the error that the exception should have.
     */
    private void checkIfNumberIsAboveZero(int numberToCheck, String prefix){
        if (numberToCheck < 0){
            throw new IllegalArgumentException("The number " + prefix + " must be above 0.");
        }
    }
}
