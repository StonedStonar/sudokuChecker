package no.ntnu.OS.sudokuApp.model;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that represents a basic sudoku board.
 * @version 0.1
 * @author Steinar Hjelle Midthus & Kenneth Misund
 */
public class SudokuBoard {

    private Map<Integer, Row> rowMap;

    private final int size;

    /**
     * Makes an instance of the SudokuBoard.
     * @param size the size this map should have. This is in the Y direction.
     */
    public SudokuBoard(int size){
        checkSize(size);
        rowMap = new HashMap<>();
        this.size = size;
        addAllRows(size);
    }

    /**
     * Makes a SudokuBoard with the solution as a string.
     * @param sudokuSolution the solution to check.
     */
    public SudokuBoard(String sudokuSolution){
        checkString(sudokuSolution, "sudoku solution");
        this.size = getDimensions(sudokuSolution);
        rowMap = new HashMap<>();
        addAllRows(size);
        parseFromStringAndAdd(sudokuSolution);
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
        for (int i = 1; i <= size; i++){
            Row row = rowMap.get(i);
            for (int j = 0; j < size; j++){
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
     * Checks if the input size is above zero.
     * @param size the size to check.
     */
    private void checkSize(int size){
        checkIfNumberIsAboveZero(size, "size");
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
     * Gets the dimensions of a string to check if its whole.
     * @param string the string to check for a nxn matrix.
     * @return the dimensions of this string. If string is 81 letters then it returns 9.
     * @throws NumberFormatException if the number is not an even square number.
     */
    private int getDimensions(String string){
        double dimensions = Math.sqrt(string.length());
        if (dimensions == (int) dimensions){
            return (int) dimensions;
        }else {
            throw new NumberFormatException("The input number must be a n x n matrix.");
        }
    }

    /**
     *
     */
    public List<SudokuNumber> checkPuzzleIsValid(ExecutorService executorService){
        ThreadList duplicateNumbers = new ThreadList();

        SudokuCheckThread cellCheckThread = new SudokuCheckThread(this,false, false);
        SudokuCheckThread columnCheckThread = new SudokuCheckThread(this, true, false);
        SudokuCheckThread rowCheckThread = new SudokuCheckThread(this,false, true);

        Future<ThreadList> futureCell = executorService.submit(cellCheckThread);
        Future<ThreadList> futureColumn = executorService.submit(columnCheckThread);
        Future<ThreadList> futureRow = executorService.submit(rowCheckThread);
        try {
            ThreadList cellDuplicates = futureCell.get();
            ThreadList columnDuplicates = futureColumn.get();
            ThreadList rowDuplicates = futureRow.get();
            System.out.println("Amount of faults: " + cellDuplicates.getAllSudokuNumbers().size());
            System.out.println("Amount of faults: " + columnDuplicates.getAllSudokuNumbers().size());
            System.out.println("Amount of faults: " + rowDuplicates.getAllSudokuNumbers().size());
            if (size < 4){
                //Todo: Make methods that checks for all the rows and columns for points out guaranteed failure.
                //Todo: Delete all guaranteed faults and return all the results.
            }else {
                //Todo: Make methods that checks all the cells, rows and columns for points out failures that are guaranteed.
                cellDuplicates.getAllSudokuNumbers().forEach(dupNum -> rowDuplicates.getAllSudokuNumbers().forEach(secondDupNum -> {
                    if (dupNum.checkIfPositionIsSame(secondDupNum) && !duplicateNumbers.containsSudokuNumber(secondDupNum)){
                        columnDuplicates.getAllSudokuNumbers().forEach(thirdSudNum -> {
                            if (thirdSudNum.checkIfPositionIsSame(secondDupNum)){
                                secondDupNum.incrementFailureRating();
                                secondDupNum.incrementFailureRating();
                                duplicateNumbers.addSudokuNumber(secondDupNum);
                            }
                        });
                    }
                }));

                //Todo: Delete all guaranteed faults and return all the results.
            }
            duplicateNumbers.getAllSudokuNumbers().forEach(test -> System.out.println(test.getListID() + " " + test.getColumnID() + " " + test.getNumber()));
            System.out.println("Same numbers :" + duplicateNumbers.getAllSudokuNumbers().size());
            combineAllThreadListsIntoDuplicateList(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return duplicateNumbers.getAllSudokuNumbers();
    }

    public static void main(String[] args) {
        SudokuBoard sudokuBoard = new SudokuBoard("4321323434122143");
        List<SudokuNumber> sudokuNumberList = sudokuBoard.checkPuzzleIsValid(Executors.newFixedThreadPool(3));
        sudokuNumberList.forEach(sudNum -> System.out.println("X " + sudNum.getColumnID() + " Y " + sudNum.getListID() + " Val " + sudNum.getNumber()));
    }
    /**
     *
     * @param rowDuplicates
     * @param columnDuplicates
     * @param cellDuplicates
     * @param duplicateNumbers
     */
    private void combineAllThreadListsIntoDuplicateList(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList duplicateNumbers){
        duplicateNumbers.getAllSudokuNumbers().forEach(sudNum -> {
            columnDuplicates.removeAllNumbersWithSameValueRowAndColumn(sudNum);
            rowDuplicates.removeAllNumbersWithSameValueRowAndColumn(sudNum);
            cellDuplicates.removeAllNumbersWithSameValueRowAndColumn(sudNum);
        });

        rowDuplicates.getAllSudokuNumbers().forEach(sudNum -> {
            if (!duplicateNumbers.containsSudokuNumber(sudNum)){
                duplicateNumbers.addSudokuNumber(sudNum);
            }
        });
        cellDuplicates.getAllSudokuNumbers().forEach(sudNum -> {
            if (!duplicateNumbers.containsSudokuNumber(sudNum)){
                duplicateNumbers.addSudokuNumber(sudNum);
            }
        });
        columnDuplicates.getAllSudokuNumbers().forEach(sudNum -> {
            if (!duplicateNumbers.containsSudokuNumber(sudNum)){
                duplicateNumbers.addSudokuNumber(sudNum);
            }
        });
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
