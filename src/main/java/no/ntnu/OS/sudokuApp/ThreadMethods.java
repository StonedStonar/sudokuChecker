package no.ntnu.OS.sudokuApp;
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

    public static void main(String[] args) {
        SudokuBoard sudoBoard = new SudokuBoard(9);
        int size = sudoBoard.getSize();
        for (int i = 1; size >= i; i++) {
            sudoBoard.getRowIterator(i); {

            }
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
