package no.ntnu.OS.sudokuApp.ui.controllers;

/**
 * Represents a general controller that is used in the loading and acting in a scene.
 * @version 0.1
 * @author Steinar Hjelle Midthus
 */
public interface Controller {

    /**
     * Updates the content on the wanted page.
     */
    void updateContent();

    /**
     * Makes all the content to reset that needs to be reset.
     */
    void emptyContent();
}
