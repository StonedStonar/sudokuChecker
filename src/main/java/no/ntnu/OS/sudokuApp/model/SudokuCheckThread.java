package no.ntnu.OS.sudokuApp.model;

import java.util.*;
import java.util.concurrent.Callable;

/**
 *
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class SudokuCheckThread implements Callable<List<SudokuNumber>> {

    private final SudokuBoard sudokuBoard;

    private boolean checkColumn;

    private boolean checkRow;

    /**
      * Makes an instance of the SudokuCheckThread class.
      */
    public SudokuCheckThread(SudokuBoard sudokuBoard, boolean checkColumn, boolean checkRow){
        checkIfObjectIsNull(sudokuBoard, "sudokuboard");
        this.sudokuBoard = sudokuBoard;
        this.checkColumn = checkColumn;
        this.checkRow = checkRow;
        if (checkColumn){
            this.checkRow = false;
        }else if (checkRow){
            this.checkColumn = false;
        }
    }

    @Override
    public List<SudokuNumber> call() throws Exception {
        List<SudokuNumber> sudokuNumberList = null;
        if (!checkColumn && !checkRow){
            sudokuNumberList = checkCells();
        }else if (checkColumn){
            sudokuNumberList = checkColumns();
        }else {
            sudokuNumberList = checkRow();
        }
        return sudokuNumberList;
    }

    /**
     * Checks the row of the puzzle and returns a list with duplicate numbers.
     * @return a list with duplicate numbers.
     */
    private List<SudokuNumber> checkRow() {
        List<SudokuNumber> duplicateNumbers = new ArrayList<>();
        Map<Integer, ThreadList> threadListMap = makeThreadListMapWithLists();
        int size = sudokuBoard.getSize();
        for (int i = 1; i <= size; i++) {
            Iterator<Integer> it = sudokuBoard.getRowIterator(i);
            ThreadList threadList = threadListMap.get(i);
            int pos = 1;
            while (it.hasNext()) {
                int nextNumber = it.next();
                SudokuNumber sudokuNumber = new SudokuNumber(i, pos, nextNumber);
                if (threadList.contains(nextNumber)) {
                    duplicateNumbers.add(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThreadList(nextNumber,threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                pos++;
            }
        }
        threadListMap.values().forEach(ThreadList::printAllElements);
        System.out.println("Row check results: " + duplicateNumbers.size());
        duplicateNumbers.forEach(number -> System.out.println("Y: " + number.getListID() + " X: " + number.getColumnID() + " Number value: " + number.getNumber()));

        return duplicateNumbers;
    }

    public static void main(String[] args) {
        SudokuCheckThread sudokuCheckThread = new SudokuCheckThread(new SudokuBoard("1212"), false, false);
        sudokuCheckThread.checkColumns();
    }

    /**
     * Checks each cell in an n x n matrix.
     * @return A list with all the sudoku numbers that are seen twice.
     */
    private List<SudokuNumber> checkCells(){
        int size = sudokuBoard.getSize();
        List<SudokuNumber> duplicateNumbers = new ArrayList<>();
        if (size >= 4){
            Map<Integer, ThreadList> threadListMap = makeThreadListMapWithLists();

            int dimensions = (int) Math.round(Math.sqrt(size));
            //Dimensjonen på brettet

            System.out.println("Dimensions : " + dimensions);

            //Skaffer alle iteratorene og legger dem i et map.
            int cellCount = 1;

            for (int i = 1; i <= size; i++){
                Iterator<Integer> it = sudokuBoard.getRowIterator(i);
                int p = 1;
                int nextList = cellCount;
                ThreadList threadList = null;
                //Map used to find the first of a duplicate. Since we can have many duplicates we need more than one
                while (it.hasNext()){
                    threadList = threadListMap.get(nextList);
                    int number = it.next();
                    SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                    if (threadList.contains(number)){
                        duplicateNumbers.add(sudokuNumber);
                        checkAndAddFirstNumberIfNotInThreadList(number, threadList, duplicateNumbers);
                    }
                    threadList.addSudokuNumber(sudokuNumber);
                    if (p % dimensions == 0){
                        nextList++;
                    }
                    p++;
                }
                if (i % dimensions == 0){
                    cellCount = cellCount + dimensions;
                }
            }

            threadListMap.values().forEach(ThreadList::printAllElements);
            System.out.println("Cell check results: ");
            duplicateNumbers.forEach(number -> System.out.println("Y: " + number.getListID() + " X: " + number.getColumnID() + " Number value: " + number.getNumber()));
        }
        return duplicateNumbers;
    }

    /**
     * Makes a thread list map with lists in it.
     * @return a map with an input for each row.
     */
    private Map<Integer, ThreadList> makeThreadListMapWithLists(){
        Map<Integer, ThreadList> threadListMap = new HashMap<>();
        for (int i = 1; i <= sudokuBoard.getSize(); i++){
            threadListMap.put(i, new ThreadList());
        }
        return threadListMap;
    }

    /**
     * Checks and adds a new number to the thread list if duplicate is found.
     * @param number the number to check after.
     * @param threadList the thread list to add to.
     * @param duplicateNumbers the list with duplicate numbers.
     */
    private void checkAndAddFirstNumberIfNotInThreadList(int number, ThreadList threadList, List<SudokuNumber> duplicateNumbers){
        SudokuNumber firstNumber = threadList.getFirstOfNumber(number);
        if (!duplicateNumbers.contains(firstNumber)){
            duplicateNumbers.add(firstNumber);
        }
    }

    /**
     * Checks if the columns are valid format. Supports n x n matrix.
     * @return A list with all the sudoku numbers that are seen twice.
     */
    public List<SudokuNumber> checkColumns(){
        int size = sudokuBoard.getSize();
        List<SudokuNumber> duplicateNumbers = new ArrayList<>();
        Map<Integer, ThreadList> threadListMap = makeThreadListMapWithLists();

        for (int i = size; i > 0; i--){
            Iterator<Integer> it = sudokuBoard.getRowIterator(i);
            int p = 1;
            while (it.hasNext()){
                ThreadList threadList = threadListMap.get(p);
                int number = it.next();
                SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                if (threadList.contains(number)){
                    duplicateNumbers.add(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThreadList(number, threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                p++;
            }
        }
        System.out.println("Column check: ");
        threadListMap.values().forEach(ThreadList::printAllElements);
        duplicateNumbers.forEach(number -> System.out.println("Y: " + number.getListID() + " X: " + number.getColumnID() + " Number value: " + number.getNumber()));
        return duplicateNumbers;
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
