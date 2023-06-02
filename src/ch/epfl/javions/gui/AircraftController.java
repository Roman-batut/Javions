package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 *  Class representing the AircraftController
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObjectProperty<ObservableAircraftState> clickedPlane;
    private final Pane pane;

     //* Constants

    private static final String AIRCRAFT_CSS_STYLE_SHEET = "aircraft.css";
    private static final String LABEL_STYLE_SHEET = "label";
    private static final String AIRCRAFT_STYLE_SHEET = "aircraft";
    private static final int ADD_SPACING_RECTANGLE = 4;
    private static final String UNKNOW_VALUE = "?";
    private static final String FORMAT_NO_DECIMAL = "%.0f";
    private static final String EN_SPACE = " ";
    private static final String SPEED_FORMAT = "\n%s km/h";
    private static final String ALTITUDE_FORMAT = "%s mètres";
    private static final String EMPTY_STRING = "";
    private static final double MAX_HEIGHT = 12000.d;
    private static final double REGUL_POWER = 1.d / 3;

    //* Constructor

    /**
     * Constructor of the AircraftController
     * @param mapParameters the mapParameters
     * @param aircraftStates the aircraftStates
     * @param clickedPlane the clickedPlane
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> clickedPlane){

        this.mapParameters = mapParameters;
        this.clickedPlane = clickedPlane;

        pane = new Pane();
        pane.setPickOnBounds(false);

        //Change Listener
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
                if(change.wasAdded()){
                    aircraft(change.getElementAdded());
                }

                if(change.wasRemoved()){
                    ObservableAircraftState statermv = change.getElementRemoved();
                    pane.getChildren()
                            .removeIf((s) -> s.getId().equals(statermv.getIcaoAddress().string()));
                }
            });
    }

    
    //* Methods

    /**
     * Gets the pane and applies the css stylesheet aircraft.css
     * @return the pane
     */
    public Pane pane(){
        pane.getStylesheets().add(AIRCRAFT_CSS_STYLE_SHEET);

        return pane;
    }

    //* Private Methods

    //Global Group

    /**
     * Sets the aircraft group and binds it to the state
     * @param state the state
     */
    private void aircraft(ObservableAircraftState state){
        Group iconLabel = iconLabel(state);

        Group trajectory = trajectory(state);

        trajectory.layoutXProperty()
                .bind(mapParameters.minX().negate());
        trajectory.layoutYProperty()
                .bind(mapParameters.minY().negate());

        Group aircraft = new Group(trajectory, iconLabel);

        aircraft.setId(state.getIcaoAddress().string());
        aircraft.viewOrderProperty()
                .bind(state.altitudeProperty().negate());

        pane.getChildren().add(aircraft);
    }
    
    //Icon + Label

    /**
     * Creates the iconLabel group with the icon and the label and binds it to the state
     * @param state the state
     * @return the group
     */
    private Group iconLabel(ObservableAircraftState state){
        SVGPath icon = icon(state);

        Group label = label(state);
        label.visibleProperty().bind(Bindings.createBooleanBinding(
            () -> state.equals(clickedPlane.get()) || mapParameters.getZoom()>=11, clickedPlane, mapParameters.zoom()));


        Group iconLabel = new Group(icon, label);
        iconLabel.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                (WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()) - mapParameters.getMinX()),
                state.positionProperty(), mapParameters.minX()));

        iconLabel.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                (WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()) - mapParameters.getMinY()),
                state.positionProperty(), mapParameters.minY()));

        return iconLabel;
    }
    
    //Label
    
    /**
     * Creates the label group with the rectangle and the text
     * @param state the state
     * @return the group
     */
    private Group label(ObservableAircraftState state){
        Text text = text(state);
        Rectangle rectangle = background(text);

        Group label = new Group(rectangle, text);

        label.getStyleClass().add(LABEL_STYLE_SHEET);

        return label;
    }
    
    /**
     * Creates a rectangle and binds it to the text
     * @param text the text
     * @return the rectangle
     */
    private Rectangle background(Text text){
        Rectangle rectangle = new Rectangle();

        rectangle.widthProperty()
                .bind(text.layoutBoundsProperty()
                        .map(b -> b.getWidth() + ADD_SPACING_RECTANGLE));
        rectangle.heightProperty()
                .bind(text.layoutBoundsProperty()
                        .map(b -> b.getHeight() + ADD_SPACING_RECTANGLE));

        return rectangle;
    }

    /**
     * Creates a text and binds it to the state
     * @param state the state
     * @return the text
     */
    private Text text(ObservableAircraftState state){

        StringBinding velAlt = Bindings.createStringBinding(() ->{
            double vel = state.velocityProperty().getValue();
            double alt = state.altitudeProperty().getValue();
            return
                String.format(SPEED_FORMAT + EN_SPACE + ALTITUDE_FORMAT,
                        Double.isNaN(vel) ?
                                UNKNOW_VALUE :
                                String.format(FORMAT_NO_DECIMAL,(Units.convertTo(vel, Units.Speed.KILOMETER_PER_HOUR))),
                        Double.isNaN(alt) ?
                                UNKNOW_VALUE : String.format(FORMAT_NO_DECIMAL,alt)
        );},state.velocityProperty(), state.altitudeProperty());

        Text text = new Text();
        if(state.getAircraftData() == null || state.getRegistration() == null){
            ObservableValue<String> id = state.callSignProperty().map(CallSign :: string)
                    .when(state.callSignProperty()
                    .isNull().not())
                    .orElse(state.getIcaoAddress().string());
            text.textProperty().bind(Bindings.createStringBinding(
                    () -> (id.getValue() + velAlt.getValue()), id, velAlt));
        }else {
            text.textProperty().bind(Bindings.createStringBinding(
                    ()-> state.getRegistration().string() + velAlt.getValue(), velAlt));
        }

        return text;
    }
    
    //Icon

    /**
     * Creates the icon and binds it to the state and the aircraftIcon
     * @param state the state
     * @return the icon
     */
    private SVGPath icon(ObservableAircraftState state){
        SVGPath icon = new SVGPath();

        AircraftIcon aircraftIcon = iconCreation(state);
        StringBinding updatedIcon = Bindings.createStringBinding(aircraftIcon::svgPath, state.categoryProperty());
        icon.contentProperty().bind(updatedIcon);

        icon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                aircraftIcon.canRotate() ?
                        Units.convertTo(state.trackOrHeading().get(), Units.Angle.DEGREE) :
                        0, state.trackOrHeading()));

        icon.fillProperty().bind(Bindings.createObjectBinding(() ->
                ColorRamp.PLASMA.at(Math.pow((state.getAltitude()/ MAX_HEIGHT), REGUL_POWER)), state.altitudeProperty()));

        icon.setOnMousePressed(e ->
                clickedPlane.set(state));

        icon.getStyleClass().add(AIRCRAFT_STYLE_SHEET);

        return icon;
    }

    /**
     * Finds the aircraftIcon corresponding to the state
     * @param state the state
     * @return the aircraftIcon
     */
    private AircraftIcon iconCreation(ObservableAircraftState state){
        AircraftData data = state.getAircraftData();

        return data != null ?
                (AircraftIcon.iconFor(state.getTypeDesignator(), state.getDescription(),
                state.getCategory(), state.getWakeTurbulenceCategory()))
                : (AircraftIcon.iconFor(new AircraftTypeDesignator(EMPTY_STRING), new AircraftDescription(EMPTY_STRING),
                state.getCategory(), WakeTurbulenceCategory.UNKNOWN));
    }
    
    //Trajectory
    
    /**
     * Creates the trajectory group and binds it to the state
     * @param state the state
     * @return the group
     */
    private Group trajectory(ObservableAircraftState state){

        Group trajectory = new Group();

        trajectory.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> state.equals(clickedPlane.getValue()), clickedPlane));

        trajectory.visibleProperty().addListener((observable, oldValue, newValue) -> {
             ListChangeListener<ObservableAircraftState.AirbornPos> changePos = (c) -> trajectoryLines(state, trajectory);
             InvalidationListener changeZoom = (c) -> trajectoryLines(state, trajectory);

             if(newValue){
                state.trajectoryProperty().addListener(changePos);
                mapParameters.zoom().addListener(changeZoom);
             }
             if(!newValue){
                state.trajectoryProperty().removeListener(changePos);
                mapParameters.zoom().removeListener(changeZoom);
             }
        });

        trajectory.getStyleClass().add("trajectory");
        return trajectory;
    }

    /**
     * Sets the trajectory lines in the trajectory group
     * @param state the state
     * @param trajectory the trajectory group
     */
    private void trajectoryLines(ObservableAircraftState state, Group trajectory){
        trajectory.getChildren().clear();

        double[] posX = new double[2];
        posX[0] = WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(0).position().longitude());
        double[] posY = new double[2];
        posY[0] = WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(0).position().latitude());

        for(int i=1 ; i<state.trajectoryProperty().size()-1 ; i++){

            posX[i%2] = WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().longitude());
            posY[i%2] = WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().latitude());
            Line line = line(posX[(i+1)%2], posY[(i+1)%2], posX[i%2], posY[i%2]);

            if(state.trajectoryProperty().get(i-1).altitude() == state.trajectoryProperty().get(i).altitude()){
                line.setStroke(ColorRamp.PLASMA.at(Math.pow((state.trajectoryProperty().get(i).altitude()/MAX_HEIGHT),1.d/3)));
            } else {
                Color color1 = ColorRamp.PLASMA.at(Math.pow((state.trajectoryProperty().get(i-1).altitude()/MAX_HEIGHT),1.d/3));
                Color color2 = ColorRamp.PLASMA.at(Math.pow((state.trajectoryProperty().get(i).altitude()/MAX_HEIGHT),1.d/3));
                line.setStroke(new LinearGradient(0,0,1,1,true,
                        CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(1, color2)));
            }

            trajectory.getChildren().add(line);
        }
    }

    /**
     * Create a trajectory line
     * @param startX the start x position
     * @param startY the start y position
     * @param endX the end x position
     * @param endY the end y position
     * @return a trajectory line
     */
    private Line line(double startX, double startY, double endX, double endY){
        Line line = new Line();

        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        return line;
    }

}
