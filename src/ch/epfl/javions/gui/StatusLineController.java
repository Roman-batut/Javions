package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Class representing a status line controller
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class StatusLineController {

    private final BorderPane pane;
    private final IntegerProperty aircraftCount;
    private final LongProperty messageCount;
    
    //* Constants

    public static final String STATUS_LINE_STYLE_SHEET = "status.css";
    public static final String DATA_VALUE_FORMAT = "%03d";
    public static final String AIRCRAFT_VISIBLE_STRING = "Aéronefs visibles : ";
    public static final String RECEIVE_MESSAGES_STRING = "Messages reçues : ";
    
    //* Constructor

    /**
     * StatusLineController's constructor
    */
    public StatusLineController(){
        pane = new BorderPane();
        pane.getStylesheets().add(STATUS_LINE_STYLE_SHEET);

        Text aircraftCountText = new Text();
        Text messageCountText = new Text();

        aircraftCount = new SimpleIntegerProperty(0);
        messageCount = new SimpleLongProperty(0);

        StringBinding aircraftCountValue = Bindings.createStringBinding(() ->
            String.format(DATA_VALUE_FORMAT,aircraftCountProperty().get()),aircraftCountProperty());
        aircraftCountText.textProperty().bind(Bindings.createStringBinding(()-> AIRCRAFT_VISIBLE_STRING + aircraftCountValue.getValue(), aircraftCountValue));

        StringBinding messageCountValue = Bindings.createStringBinding(() ->
                String.format(DATA_VALUE_FORMAT,messageCountProperty().get()),messageCountProperty());
        messageCountText.textProperty().bind(Bindings.createStringBinding(()-> RECEIVE_MESSAGES_STRING + messageCountValue.getValue(), messageCountValue));

        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    //* Getters

    /**
     * Getter of the pane
     * @return the pane
     */
    public BorderPane pane(){
        return pane;
    }

    /**
     * Getter of the aircraftCount
     * @return the aircraftCount
     */
    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }

    /**
     * Getter of the messageCount
     * @return the messageCount
     */
    public LongProperty messageCountProperty(){
        return messageCount;
    }

}

// #TODO les constantes sont publiques ?