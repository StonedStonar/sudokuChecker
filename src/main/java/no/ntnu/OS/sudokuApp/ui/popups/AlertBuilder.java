package no.ntnu.OS.sudokuApp.ui.popups;

import javafx.scene.control.Alert;

/**
 * A builder class for alerts.
 * @version 0.1
 * @author Group 13
 */
public class AlertBuilder {

    private Alert alert;

    /**
      * Makes an instance of the AlertErrorBuilder class.
      */
    public AlertBuilder(Alert.AlertType alertType){
        alert = new Alert(alertType);
    }

    /**
     * Sets the title of the alert.
     * @param title the title of the alert.
     * @return this builder object.
     */
    public AlertBuilder setTitle(String title){
        checkString(title, "title");
        alert.setTitle(title);
        return this;
    }

    /**
     * Sets the header text of the alert.
     * @param headerText the new header text.
     * @return this builder object.
     */
    public AlertBuilder setHeaderText(String headerText){
        checkString(headerText, "header text");
        alert.setHeaderText(headerText);
        return this;
    }

    /**
     * Sets the context of the alert.
     * @param context the new context.
     * @return this builder object.
     */
    public AlertBuilder setContext(String context){
        checkString(context, "context");
        alert.setContentText(context);
        return this;
    }

    /**
     * Makes the alert object and returns it.
     * @return the new defined alert.
     */
    public Alert build(){
        return alert;
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
