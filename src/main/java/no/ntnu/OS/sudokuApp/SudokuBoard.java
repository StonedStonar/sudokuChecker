package no.ntnu.OS.sudokuApp;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that represents a basic sudoku board.
 * @version 0.1
 * @author Steinar Hjelle Midthus & Kenneth Misund
 */
public class SudokuBoard {

    private Map<Integer, Row> rowMap;

    private int size;

    /**
     * Makes an instance of the SudokuBoard.
     * @param size the size this map should have. This is in the Y direction.
     */
    public SudokuBoard(int size){
        checkIfNumberIsAboveZero(size, "size");
        rowMap = new HashMap<>();
        this.size = size;
        addAllRows(size);
    }

    public SudokuBoard(String sudokuSolution){
        checkString(sudokuSolution, "sudoku solution");
        rowMap = new HashMap<>();
        addAllRows(9);
        parseFromStringAndAdd(sudokuSolution);
    }

    public static void main(String[] args) {
        SudokuBoard sudokuBoard = new SudokuBoard("864371259325849761971265843436192587198657432257483916689734125713528694542916378");
        sudokuBoard.printAllRows();
    }

    /**
     * Prints all the rows in a "grid" fashion that can be used for testing and visualsation.
     */
    public void printAllRows(){
        AtomicInteger i = new AtomicInteger(1);
        System.out.println("Plass  1 2 3 4 5 6 7 8 9");
        System.out.println("________________________");
        rowMap.values().forEach(row -> {
            System.out.print("Row " + i + ": ");
            row.getIterator().forEachRemaining(element -> {
                System.out.print(element + " ");
            });
            System.out.println();
            i.addAndGet(1);
        });
    }

    /**
     * Parses the values of this table from an input string.
     * @param sudokuSolution the sudoku solution to parse.
     */
    private void parseFromStringAndAdd(String sudokuSolution){
        List<Integer> numbers = parseNumbersFromStringToList(sudokuSolution);
        int numberToAdd = 0;
        for (int i = 1; i <= 9; i++){
            Row row = rowMap.get(i);
            for (int j = 0; j < 9; j++){
                row.addNumber(numbers.get(numberToAdd));
                numberToAdd++;
            }
        }
    }

    /**
     * Splits the string into all its digits and adds each number to a list.
     * @param stringToParse the string to make into a list of numbers.
     * @return the list with all the numbers.
     */
    private List<Integer> parseNumbersFromStringToList(String stringToParse){
        List<Integer> list = new ArrayList<>();
        String[] letters = stringToParse.split("");
        Arrays.stream(letters).forEach(letter -> list.add(Integer.parseInt(letter)));
        return list;
    }

    /**
     * Adds all the wanted rows to the map.
     * @param size the size of the board.
     */
    private void addAllRows(int size){
        for (int i = 1; i <= size; i++) {
            Row row = new Row(size);
            addRow(row, i);
        }
    }

    /**
     * Adds a row to the sudoku board.
     * @param row adds a row to the
     * @param ID the ID of the row.
     */
    private void addRow(Row row, int ID){
        rowMap.put(ID, row);
    }

    /**
     * Gets the iterator of a row in the
     * @param rowID the ID of the row.
     * @return the rows ID.
     */
    public synchronized Iterator<Integer> getRowIterator(int rowID){
        checkRowID(rowID);
        return rowMap.get(rowID).getIterator();
    }

    /**
     * Gets the size of the row-map.
     * @return the size of the row map.
     */
    public synchronized int getSize(){
        return rowMap.size();
    }


    /**
     * Checks if the row id is above zero.
     * @param rowID the row id.
     */
    private void checkRowID(int rowID){
        checkIfNumberIsAboveZero(rowID, "row id");
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
