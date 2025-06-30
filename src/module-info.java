/**
 * 
 */
/**
 * 
 */
module JavaFXApp {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    
    opens uygulama to javafx.graphics, javafx.fxml;
}