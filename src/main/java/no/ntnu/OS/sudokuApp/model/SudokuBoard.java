package no.ntnu.OS.sudokuApp.model;

import javafx.beans.Observable;
import no.ntnu.OS.sudokuApp.ui.SudokuApp;

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
public class SudokuBoard implements ObservableSudokuBoard {

    private Map<Integer, Row> rowMap;

    private final int size;

    private List<SudokuBoardObserver> sudokuBoardObservers;

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
        sudokuBoardObservers =  new LinkedList<>();
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
     * Checks if the sudoku puzzle is valid.
     * @param executorService
     */
    public void checkPuzzleIsValid(ExecutorService executorService){
        ThreadList duplicateNumbers = new ThreadList();
        ThreadList secondDuplicateList = new ThreadList();

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
                combineTwoAndTwoLists(rowDuplicates, columnDuplicates, duplicateNumbers);
            }else {
                //Todo: Make methods that checks all the cells, rows and columns for points out failures that are guaranteed.
                compareAllThreeListsIntoOne(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);

                //Todo: Delete all guaranteed faults and return all the results. - Done
            }
            duplicateNumbers.getAllSudokuNumbers().forEach(test -> System.out.println(test.getListID() + " " + test.getColumnID() + " " + test.getNumber()));
            System.out.println("Same numbers :" + duplicateNumbers.getAllSudokuNumbers().size());
            combineAllThreadListsIntoDuplicateList(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);

            combineRestThreadLists(rowDuplicates, columnDuplicates, cellDuplicates, secondDuplicateList);
            addRemainingSudoNumbers(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        List<SudokuNumber> totalFaults = duplicateNumbers.getAllSudokuNumbers();
        totalFaults.addAll(secondDuplicateList.getAllSudokuNumbers());
        notifyObservers(duplicateNumbers);
    }

    private void combineRestThreadLists(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList newDuplicateList){
        combineTwoAndTwoLists(rowDuplicates, columnDuplicates, newDuplicateList);
        combineTwoAndTwoLists(rowDuplicates, cellDuplicates, newDuplicateList);
        combineTwoAndTwoLists(cellDuplicates, columnDuplicates, newDuplicateList);
        List<SudokuNumber> secondDuplicate = newDuplicateList.getAllSudokuNumbers();
        rowDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(secondDuplicate, this, false);
        columnDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(secondDuplicate, this, false);
        cellDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(secondDuplicate, this, true);

    }

    /**
     * Combines two threadlsists into one.
     * @param firstList the first list.
     * @param secondList the second list.
     * @param duplicateNumbers the last list.
     */
    private void combineTwoAndTwoLists(ThreadList firstList, ThreadList secondList, ThreadList duplicateNumbers){
        firstList.getAllSudokuNumbers().forEach(errorNum -> secondList.getAllSudokuNumbers().forEach(secondErrorNum -> {
            if (secondErrorNum.checkIfPositionIsSame(errorNum) && !duplicateNumbers.containsSudokuNumber(errorNum)){
                errorNum.incrementFailureRating();
                duplicateNumbers.addSudokuNumber(errorNum);
            }
        }));
    }

    /**
     * Combines all threadlists into one duplicate list.
     * @param rowDuplicates the row duplicates.
     * @param columnDuplicates the column duplicates
     * @param cellDuplicates the cell duplicates
     * @param duplicateNumbers the wanted duplicate numbers list.
     */
    private void combineAllThreadListsIntoDuplicateList(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList duplicateNumbers){
        List<SudokuNumber> duplicateSudoNumbers = duplicateNumbers.getAllSudokuNumbers();
        columnDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(duplicateSudoNumbers, this, false);
        rowDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(duplicateSudoNumbers, this, false);
        cellDuplicates.removeAllNumbersWithSameValueRowAndColumnAndAlsoCell(duplicateSudoNumbers, this, true);
    }

    private void addRemainingSudoNumbers(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList duplicateNumbers){
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
     * Compares all the three lists and finds what they have in common. Also removes the already selected numbers.
     * @param rowDuplicates the row duplicates.
     * @param columnDuplicates the column duplicates.
     * @param cellDuplicates the cell duplicates
     * @param duplicateNumbers the duplicate numbers that
     */
    private void compareAllThreeListsIntoOne(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList duplicateNumbers){
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

    @Override
    public void registerObserver(SudokuBoardObserver sudokuBoardObserver) {
        checkIfObserverIsNull(sudokuBoardObserver);
        this.sudokuBoardObservers.add(sudokuBoardObserver);
    }

    @Override
    public void removeObserver(SudokuBoardObserver sudokuBoardObserver) {
        checkIfObserverIsNull(sudokuBoardObserver);
        this.sudokuBoardObservers.remove(sudokuBoardObserver);
    }

    @Override
    public void notifyObservers(ThreadList threadList) {
        checkIfObjectIsNull(threadList, "threadlist");
        this.sudokuBoardObservers.forEach(observer -> observer.update(threadList.getAllSudokuNumbers()));
    }

    /**
     * Checks if the observer is null.
     * @param sudokuBoardObserver the sudoku-board observer to check.
     */
    private void checkIfObserverIsNull(SudokuBoardObserver sudokuBoardObserver){
        checkIfObjectIsNull(sudokuBoardObserver, "sudoku-board observer");
    }
}
