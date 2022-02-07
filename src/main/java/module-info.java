module sudokuApp {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;

    opens no.ntnu.OS.sudokuApp.ui.controllers to javafx.fxml, javafx.graphics;
    opens no.ntnu.OS.sudokuApp.ui.windows to javafx.graphics, javafx.fxml;
    opens no.ntnu.OS.sudokuApp.model to javafx.fxml, javafx.graphics;
    opens no.ntnu.OS.sudokuApp.ui to javafx.graphics, javafx.fxml;

    exports no.ntnu.OS.sudokuApp.ui.controllers;
    exports no.ntnu.OS.sudokuApp.ui.windows;
    exports no.ntnu.OS.sudokuApp.model;
    exports no.ntnu.OS.sudokuApp.ui;

}