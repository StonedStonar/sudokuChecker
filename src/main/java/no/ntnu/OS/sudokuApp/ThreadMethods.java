package no.ntnu.OS.sudokuApp;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 *
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public class ThreadMethods {

    /**
      * Makes an instance of the ThreadMethods class.
      */
    public ThreadMethods(){
    
    }

    public static void main(String[] args){
        SudokuBoard sudokuBoard = new SudokuBoard("346179258187523964529648371965832417472916835813754629798261543631485792254397186", 9);
        test(sudokuBoard);



    }

    /**
     * Represents a basic method to calculate
     * @param sudokuBoard
     */
    private static void test(SudokuBoard sudokuBoard){
        SudokuCheckThread cellCheckThread = new SudokuCheckThread(sudokuBoard,false, false);
        SudokuCheckThread columnCheckThread = new SudokuCheckThread(sudokuBoard, true, false);
        SudokuCheckThread rowCheckThread = new SudokuCheckThread(sudokuBoard,false, true);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<List<SudokuNumber>> futureCell = executorService.submit(cellCheckThread);
        Future<List<SudokuNumber>> futureColumn = executorService.submit(columnCheckThread);
        Future<List<SudokuNumber>> futureRow = executorService.submit(rowCheckThread);
        try {
            List<SudokuNumber> cellDuplicates = futureCell.get();
            List<SudokuNumber> columnDuplicates = futureColumn.get();
            List<SudokuNumber> rowDuplicates = futureRow.get();
            System.out.println("Amount of faults: " + cellDuplicates.size());
            System.out.println("Amount of faults " + columnDuplicates.size());
            System.out.println("Amount of faults after code " + "s");
            ThreadList threadList = new ThreadList();
            cellDuplicates.forEach(errorNumber -> columnDuplicates.forEach(secondErrorNumber -> {
                if (secondErrorNumber.checkIfPositionIsSame(errorNumber) && !threadList.containsSudokuNumber(secondErrorNumber)){
                    rowDuplicates.forEach(thirdErrorNumber -> {
                        if (secondErrorNumber.checkIfPositionIsSame(thirdErrorNumber)){
                            threadList.addSudokuNumber(thirdErrorNumber);
                        }
                    });
                }
            }));
            threadList.getAllSudokuNumbers().forEach(test -> System.out.println(test.getListID() + " " + test.getColumnID() + " " + test.getNumber()));
            System.out.println("Same numbers :" + threadList.getAllSudokuNumbers().size());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
