package no.ntnu.OS.sudokuApp;

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

        }
        return sudokuNumberList;
    }

    private List<SudokuNumber> checkCells(){
        int size = sudokuBoard.getSize();
        List<SudokuNumber> duplicateNumbers = new ArrayList<>();
        Map<Integer, ThreadList> threadListMap = new HashMap<>();

        //Dimensjonen på brettet
        int dimensions = (int) Math.round(Math.sqrt(size));

        System.out.println("Dimensions : " + dimensions);

        //Skaffer alle iteratorene og legger dem i et map.

        for (int i = size; i > 0; i--){
            threadListMap.put(i, new ThreadList());
        }

        int cellCount = 1;

        for (int i = 1; i <= size; i++){
            Iterator<Integer> it = sudokuBoard.getRowIterator(i);
            int p = 1;
            int nextList = cellCount;
            ThreadList threadList = threadListMap.get(nextList);
            //Map used to find the first of a duplicate. Since we can have many duplicates we need more than one
            while (it.hasNext()){
                int number = it.next();
                SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                if (threadList.contains(number)){
                    duplicateNumbers.add(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThread(number, threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                if (p % dimensions == 0){
                    nextList++;
                }
                p++;
                threadList = threadListMap.get(nextList);
            }
            if (i % dimensions == 0){
                cellCount = cellCount + dimensions;
            }
        }

        threadListMap.values().forEach(ThreadList::printAllElements);
        System.out.println("Cell check results: ");
        duplicateNumbers.forEach(number -> System.out.println(number.getNumber()));
        return duplicateNumbers;
    }

    /**
     *
     * @param number
     * @param threadList
     * @param duplicateNumbers
     */
    private void checkAndAddFirstNumberIfNotInThread(int number, ThreadList threadList, List<SudokuNumber> duplicateNumbers){
        SudokuNumber firstNumber = threadList.getFirstOfNumber(number);
        if (!duplicateNumbers.contains(firstNumber)){
            duplicateNumbers.add(firstNumber);
        }
    }


    public List<SudokuNumber> checkColumns(){
        int size = sudokuBoard.getSize();
        List<SudokuNumber> duplicateNumbers = new ArrayList<>();
        Map<Integer, ThreadList> threadListMap = new HashMap<>();

        for (int i = size; i > 0; i--){
            threadListMap.put(i, new ThreadList());
        }

        for (int i = size; i > 0; i--){
            Iterator<Integer> it = sudokuBoard.getRowIterator(i);
            int p = 1;
            while (it.hasNext()){
                ThreadList threadList = threadListMap.get(p);
                int number = it.next();
                SudokuNumber sudokuNumber = new SudokuNumber(i, p, number);
                if (threadList.contains(number)){
                    duplicateNumbers.add(sudokuNumber);
                    checkAndAddFirstNumberIfNotInThread(number, threadList, duplicateNumbers);
                }
                threadList.addSudokuNumber(sudokuNumber);
                p++;
            }
        }
        System.out.println("Column check: ");
        threadListMap.values().forEach(ThreadList::printAllElements);
        duplicateNumbers.forEach(number -> System.out.println(number.getNumber()));
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
