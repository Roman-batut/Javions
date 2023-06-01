package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

/**
 * Class representing the map parameters
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class MapParameters {

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    //* Constants

    private static final int MAX_ZOOM = 6;
    private static final int MIN_ZOOM = 19;

    //* Constructor

    /**
     * MapParameters' constructor
     * @param initZoom the initial zoom
     * @param initMinX the initial minX
     * @param initMinY the initial minY
     */
    public MapParameters(int initZoom, double initMinX, double initMinY){
        Preconditions.checkArgument(MAX_ZOOM <= initZoom && initZoom <= MIN_ZOOM);

        zoom = new SimpleIntegerProperty(initZoom);
        minX = new SimpleDoubleProperty(initMinX);
        minY = new SimpleDoubleProperty(initMinY);
    }

    //* View Getters

    /**
     * View getter of the zoom
     * @return the zoom
     */
    public ReadOnlyIntegerProperty zoom(){ return zoom; }

    /**
     * View getter of the minX
     * @return the minX
     */
    public ReadOnlyDoubleProperty minX(){ return minX; }

    /**
     * View getter of the minY
     * @return the minY
     */
    public ReadOnlyDoubleProperty minY(){ return minY; }

    //* Getters

    /**
     * Getter of the zoom
     * @return the zoom
     */
    public int getZoom(){ return zoom.get(); }

    /**
     * Getter of the minX
     * @return the minX
     */
    public double getMinX(){ return minX.get(); }

    /**
     * Getter of the minY
     * @return the minY
     */
    public double getMinY(){ return minY.get(); }


    //* Methods

    /**
     * Scrolls the map by a vector (x,y)
     * @param x the x coordinate of the vector
     * @param y the y coordinate of the vector
     */
    public void scroll(double x, double y){
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);
    }

    /**
     * Changes the zoom level by zoomChange
     * @param zoomChange the zoom change
     */
    public void changeZoomLevel(int zoomChange){
        int zoom = Math2.clamp(MAX_ZOOM, this.zoom.get() + zoomChange, MIN_ZOOM);
        int offset = zoom - this.zoom.get();

        minX.set(getMinX() * Math.scalb(1d,offset));
        minY.set(getMinY() * Math.scalb(1d,offset));

        this.zoom.set(zoom);
    }

}
