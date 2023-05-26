package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Math2;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import java.io.IOException;

/**
 * Class representing a base map controller
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class BaseMapController {

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    //* Constructor
    
    /**
     * BaseMapController's constructor
     * @param tileManager the tile manager
     * @param mapParameters the map parameters
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        redrawNeeded = false;

        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.widthProperty().addListener(listener -> redrawOnNextPulse());
        canvas.heightProperty().addListener(listener -> redrawOnNextPulse());

        //Mouse Scroll
        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;

            minScrollTime.set(currentTime + 100);

            double x = e.getX();
            double y = e.getY();
            mapParameters.scroll(x, y);
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-x, -y);
            redrawOnNextPulse();
        });

        //Mouse Pressed
        DoubleProperty lastX = new SimpleDoubleProperty();
        DoubleProperty lastY = new SimpleDoubleProperty();
        pane.setOnMousePressed(e -> {
            lastX.set(e.getX());
            lastY.set(e.getY());
        });

        //Mouse Dragged
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(-(e.getX() - lastX.get()), -(e.getY() - lastY.get()));
            lastX.set(e.getX());
            lastY.set(e.getY());

            redrawOnNextPulse();
        });
    }

    //* Getters

    /**
     * Getter for the pane
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }

    
    //* Methods

    /**
     * Method to set the center on a given position
     * @param geoPos the position
     */
    public void centerOn(GeoPos geoPos){
       double coordX = WebMercator.x(mapParameters.getZoom(), geoPos.longitude());
       double coordY = WebMercator.y(mapParameters.getZoom(), geoPos.latitude());

       double vectorX = coordX - mapParameters.getMinX() - (pane.getWidth()/2);
       double vectorY = coordY - mapParameters.getMinY() - (pane.getHeight()/2);

       mapParameters.scroll(vectorX, vectorY);
       redrawOnNextPulse();
    }

    //* Private methods

    /**
     * Method to redraw the map if needed
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        int xSize = (int) Math.ceil(canvas.getWidth()/256)+1;
        int ySize = (int) Math.ceil(canvas.getHeight()/256)+1;

        int tileX = (int) Math.floor(mapParameters.getMinX()/256);
        double offsetX = tileX*256 - mapParameters.getMinX();
        int tileY = (int) Math.floor(mapParameters.getMinY()/256);
        double offsetY = tileY*256 - mapParameters.getMinY();

        for (int x=0 ; x<xSize ; x++) {
            for (int y=0 ; y<ySize ; y++) {
                try {
                    if(TileManager.TileId.isValid(mapParameters.getZoom(), tileX+x, tileY+y)){
                        graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(mapParameters.getZoom(), tileX+x, tileY+y)), x*256+offsetX, y*256+offsetY);
                    }
                } catch (IOException e){
                }
            }
        }
    }

    /**
     * Method to redraw on the next pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;

        Platform.requestNextPulse();
    }
}