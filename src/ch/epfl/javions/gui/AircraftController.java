package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public final class AircraftController {

    private MapParameters mapParameters;
    private ObservableSet<ObservableAircraftState> aircraftStates;
    private  ObjectProperty<ObservableAircraftState> javaFxproperty;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> javaFxproperty){
        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.javaFxproperty = javaFxproperty;
    }

    public Pane pane(){

    }

    private Rectangle background(){
//!        Create the rectangle for the etiquette
        return null;
    }

    private Text text(){
//!        Create the text for the etiquette
        return null;
    }

    private Line line(){
//!        Create one line for the trajectory
//!        take something in argument
        return null;
    }

    private SVGPath icon(){
//!     the svg path for the icon
        return null;
    }

    private Group etiquette(){
//        ! etiquette with element
        return null;
    }

    private Group eticon(){
//        ! faire la jonction entre element
        return null;
    }

    private Group trajectory(){
//        ! faire la jonction entre lines
        return null;
    }

    private Group trajeettoutettout(){
//        ! faire la jonction entre traj et eticon
        return null;
    }



}


//#TODO rename en anglais bien correcte