package no.ntnu.OS.sudokuApp.model;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * A class that represents a basic sudoku board.
 * @version 0.1
 * @author Group 13
 */
public class SudokuBoard implements ObservableSudokuBoard {

    private final Map<Integer, Row> rowMap;

    private final int size;

    private final List<SudokuBoardObserver> sudokuBoardObservers;

    /**
     * Makes a SudokuBoard with the solution as a string.
     * @param sudokuSolution the solution to check.
     */
    public SudokuBoard(String sudokuSolution){
        checkString(sudokuSolution, "sudoku solution");
        this.size = getDimensions(sudokuSolution);
        rowMap = new HashMap<>();
        addAllRows(size);
        parseFromStringAndAddToBoard(sudokuSolution);
        sudokuBoardObservers =  new LinkedList<>();
    }

    /**
     * Makes an instance of the Sudoku board.
     * @param rows a list with all the rows. Must be nxn
     */
    public SudokuBoard(List<Row> rows){
        checkIfObjectIsNull(rows, "list with rows");
        rowMap = new HashMap<>();
        this.size = rows.size();
        sudokuBoardObservers = new LinkedList<>();
        if (checkIfRowListIsNxN(rows)){
            for (int i = 1; i <= rows.size(); i++) {
                rowMap.put(i, rows.get(i-1));
            }
        }else {
            throw new IllegalArgumentException("The input rows must be NxN in size.");
        }
    }

    /**
     * Checks if the row list is NxN in size.
     * @param rows the list with rows to check.
     * @return <code>true</code> if all the rows are the same size as the input.
     *         <code>false</code> if all the rows are not the same size as the input.
     */
    private boolean checkIfRowListIsNxN(List<Row> rows){
        return rows.stream().allMatch(row -> row.getSizeRow() == rows.size());
    }



    /**
     * Parses the values of this table from an input string.
     * @param sudokuSolution the sudoku solution to parse.
     */
    private void parseFromStringAndAddToBoard(String sudokuSolution){
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
     * Gets the dimensions of a string to check if it's a nxn board.
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
     * @param executorService the executor service to submit the threads on.
     * @param delay <code>true</code> if there is going to be a 3-second delay before the check is executed.
     *              <code>false</code> if there is not going to be a delay.
     */
    public void checkPuzzleIsValid(ExecutorService executorService, boolean delay){
        if (delay){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            getAllNumbersOverLimit(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);
            if (size < 4){
                combineTwoAndTwoLists(rowDuplicates, columnDuplicates, duplicateNumbers);
            }else {
                compareAllThreeListsIntoOne(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);
            }
            combineAllThreadListsIntoDuplicateList(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);

            combineRestThreadLists(rowDuplicates, columnDuplicates, cellDuplicates, secondDuplicateList);
            addRemainingSudoNumbers(rowDuplicates, columnDuplicates, cellDuplicates, duplicateNumbers);

            List<SudokuNumber> totalFaults = duplicateNumbers.getAllSudokuNumbers();
            totalFaults.addAll(secondDuplicateList.getAllSudokuNumbers());
            notifyObservers(duplicateNumbers);
        } catch (ExecutionException | InterruptedException e) {
            notifyAboutError();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets all the numbers that are above the max limit.
     * @param rowDuplicates the row duplicates.
     * @param columnDuplicates the column duplicates.
     * @param cellDuplicates the cell duplicates.
     * @param duplicateList the duplicate list.
     */
    private void getAllNumbersOverLimit(ThreadList rowDuplicates, ThreadList columnDuplicates, ThreadList cellDuplicates, ThreadList duplicateList){
        List<SudokuNumber> rowDup = rowDuplicates.getAllInvalidSudokuNumbers();
        List<SudokuNumber> colDup = columnDuplicates.getAllInvalidSudokuNumbers();
        List<SudokuNumber> cellDup = cellDuplicates.getAllInvalidSudokuNumbers();

        combineListAndThreadList(rowDup, duplicateList);
        combineListAndThreadList(colDup, duplicateList);
        combineListAndThreadList(cellDup, duplicateList);
    }

    private void combineListAndThreadList(List<SudokuNumber> sudokuNumbers, ThreadList threadList){
        sudokuNumbers.forEach(number -> {
            if (!threadList.containsSudokuNumber(number)){
                threadList.addSudokuNumber(number);
            }
        });
    }


    /**
     * Combines all the lists into different lists to look for errors that are on two checks.
     * @param rowDuplicates the row duplicates.
     * @param columnDuplicates the column duplicates.
     * @param cellDuplicates the cell duplicates.
     * @param newDuplicateList the new duplicate list.
     */
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
     * Combines two thread lists into one. If the two lists has one number in common it will add it to the duplicate numbers list.
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
     * Combines all thread lists into one duplicate list.
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

    /**
     * Adds all the remaining error numbers to the duplicate numbers list.
     * @param rowDuplicates the rows duplicate numbers.
     * @param columnDuplicates the columns duplicate numbers.
     * @param cellDuplicates the cells duplicate numbers.
     * @param duplicateNumbers the duplicate numbers.
     */
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

    @Override
    public void notifyAboutError() {
        sudokuBoardObservers.forEach(SudokuBoardObserver::indicateError);
    }

    /**
     * Checks if the observer is null.
     * @param sudokuBoardObserver the sudoku-board observer to check.
     */
    private void checkIfObserverIsNull(SudokuBoardObserver sudokuBoardObserver){
        checkIfObjectIsNull(sudokuBoardObserver, "sudoku-board observer");
    }
}
