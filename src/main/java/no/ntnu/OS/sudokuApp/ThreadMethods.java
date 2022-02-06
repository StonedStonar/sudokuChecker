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
        SudokuBoard sudokuBoard = new SudokuBoard("364371259325849761971265843436192587198657432257483916689734125713528694542916378", 9);
        test(sudokuBoard);



    }

    private static void frræ(SudokuBoard sudokuBoard){
        SudokuCheckThread sudokuCheckThread = new SudokuCheckThread(sudokuBoard,false, false);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<List<SudokuNumber>> futureList = executorService.submit(sudokuCheckThread);
    }

    private static void test(SudokuBoard sudokuBoard){
        SudokuCheckThread sudokuCheckThread = new SudokuCheckThread(sudokuBoard,false, false);
        SudokuCheckThread sudokuCheckThread1 = new SudokuCheckThread(sudokuBoard, true, false);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<List<SudokuNumber>> futureList = executorService.submit(sudokuCheckThread);
        Future<List<SudokuNumber>> futureList2 = executorService.submit(sudokuCheckThread1);
        try {
            List<SudokuNumber> sudokuNumberList = futureList.get();
            List<SudokuNumber> sudList = futureList2.get();
            System.out.println("Amount of faults: " + sudokuNumberList.size());
            System.out.println("Amount of faults " + sudList.size());
            System.out.println("Amount of fautls after code " + "s");
            ThreadList threadList = new ThreadList();
            sudokuNumberList.forEach(num -> sudList.forEach(num2 -> {
                if (num2.checkIfPositionIsSame(num) && !threadList.containsSudokuNumber(num2)){
                    threadList.addSudokuNumber(num2);
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
