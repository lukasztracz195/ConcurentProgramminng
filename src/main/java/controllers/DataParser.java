package controllers;

import javafx.scene.control.TextField;

public class DataParser {

    private DataParser(){

    }

    public static double parseDataFromTextFieldToDouble(TextField textField ){
        return Double.valueOf(textField.getText());
    }

    public static long parseDataFromTextFieldToLong(TextField textField ){
        return Long.valueOf(textField.getText());
    }

    public static int parseDataFromTextFieldToInt(TextField textField ){
        return Integer.valueOf(textField.getText());
    }
}
