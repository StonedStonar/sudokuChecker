package no.ntnu.OS.sudokuApp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a class that holds a row and numbers in it.
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class Row {

    private ArrayList<Integer> rowList;

    private int size;

    /**
     * Makes an instance of the row class.
     * @param size the max size this row should have. This is in the X direction.
     */
    public Row(int size){
        checkIfNumberIsAboveZero(size, "size");
        rowList = new ArrayList<>(size);
        this.size = size;
    }

    /**
     * Adds a number to the specified place in the row.
     * @param place the spot the number should be put in.
     * @param number the number to put in that spot.
     */
    public void addNumberByPlace(int place, int number){
        checkNumberToAdd(number);
        checkPlace(place);
        rowList.add(place, number);
    }

    /**
     * Adds a number to the row.
     * @param numberToAdd the number you want to add to the row.
     */
    public void addNumber(int numberToAdd){
        checkNumberToAdd(numberToAdd);
        rowList.add(numberToAdd);
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
         return rowList.size();
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
     * Checks if the place is above zero.
     * @param place the place you want to check.
     */
    private void checkPlace(int place){
        checkIfNumberIsValid(place, 0, size-1, "place");
    }

    /**
     * Checks if the number to add is above zero.
     * @param numberToAdd the number you want to add.
     *
     */
    private void checkNumberToAdd(int numberToAdd){
        checkIfNumberIsValid(numberToAdd, 0, size, "number to add");
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
