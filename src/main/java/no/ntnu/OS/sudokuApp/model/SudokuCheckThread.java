package no.ntnu.OS.sudokuApp.model;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Represents a sudoku check thread that checks the input board for errors.
 * @version 0.1
 * @author Group 13
 */
public class SudokuCheckThread implements Callable<ThreadList> {

    private final SudokuBoard sudokuBoard;

    private boolean checkColumn;

    private boolean checkRow;

    /**
      * Makes an instance of the SudokuCheckThread class.
      *  If both input booleans are false then this thread checks the cells.
      * @param sudokuBoard the sudoku board this thread is about to check.
      * @param checkColumn <code>true</code> if the thread is going to check the columns of the board.
      *                    <code>false</code> if the thread is not going to check the columns.
      * @param checkRow  <code>true</code> if the thread is going to check the rows of the sudoku board.
     *                   <code>false</code> if the thread is not going to check the rows.
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
    public ThreadList call() throws Exception {
        ThreadList threadList = null;
        if (!checkColumn && !checkRow){
            threadList = checkCells();
        }else if (checkColumn){
            threadList = checkColumns();
        }else {
            threadList = checkRow();
        }
        return threadList;
    }

    /**
     * Checks the row of the puzzle and returns a list with duplicate numbers.
     * @return a list with duplicate numbers.
     */
    private ThreadList checkRow() {
        ThreadList duplicateNumbers = new ThreadList();
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
                    duplicateNumbers.addSudokuNumber(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThreadList(nextNumber,threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                pos++;
            }
        }
        return duplicateNumbers;
    }

    /**
     * Checks each cell in an n x n matrix.
     * @return A list with all the sudoku numbers that are seen twice.
     */
    private ThreadList checkCells(){
        int size = sudokuBoard.getSize();
        ThreadList duplicateNumbers = new ThreadList();
        if (size >= 4){
            Map<Integer, ThreadList> threadListMap = makeThreadListMapWithLists();

            int dimensions = (int) Math.round(Math.sqrt(size));

            //Skaffer alle iteratorene og legger dem i et map.
            int cellCount = 1;

            for (int i = 1; i <= size; i++){
                Iterator<Integer> it = sudokuBoard.getRowIterator(i);
                int p = 1;
                int nextList = cellCount;
                ThreadList threadList;
                //Map used to find the first of a duplicate. Since we can have many duplicates we need more than one
                while (it.hasNext()){
                    threadList = threadListMap.get(nextList);
                    int number = it.next();
                    SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                    if (threadList.contains(number)){
                        duplicateNumbers.addSudokuNumber(sudokuNumber);
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
     * @param duplicateNumbers the object with all the duplicate numbers.
     */
    private void checkAndAddFirstNumberIfNotInThreadList(int number, ThreadList threadList, ThreadList duplicateNumbers){
        SudokuNumber firstNumber = threadList.getFirstOfNumber(number);
        if (!duplicateNumbers.containsSudokuNumber(firstNumber)){
            duplicateNumbers.addSudokuNumber(firstNumber);
        }
    }

    /**
     * Checks if the columns are valid format. Supports n x n matrix.
     * @return A list with all the sudoku numbers that are seen more than once.
     */
    public ThreadList checkColumns(){
        int size = sudokuBoard.getSize();
        ThreadList duplicateNumbers = new ThreadList();
        Map<Integer, ThreadList> threadListMap = makeThreadListMapWithLists();

        for (int i = size; i > 0; i--){
            Iterator<Integer> it = sudokuBoard.getRowIterator(i);
            int p = 1;
            while (it.hasNext()){
                ThreadList threadList = threadListMap.get(p);
                int number = it.next();
                SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                if (threadList.contains(number)){
                    duplicateNumbers.addSudokuNumber(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThreadList(number, threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                p++;
            }
        }
        return duplicateNumbers;
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
