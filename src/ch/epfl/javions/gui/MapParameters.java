package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {
    private IntegerProperty zoom;
    private DoubleProperty minX;
    private DoubleProperty minY;

    public MapParameters(int initZoom, double initMinX, double initMinY){
        Preconditions.checkArgument(6<=initZoom && initZoom<=19);

        zoom = new SimpleIntegerProperty(initZoom);
        minX = new SimpleDoubleProperty(initMinX);
        minY = new SimpleDoubleProperty(initMinY);
    }

    //View Getters
    public ReadOnlyIntegerProperty zoom(){ return zoom; }
    public ReadOnlyDoubleProperty minX(){ return minX; }
    public ReadOnlyDoubleProperty minY(){ return minY; }

    //Getters
    public int getZoom(){ return zoom.get(); }
    public double getMinX(){ return minX.get(); }
    public double getMinY(){ return minY.get(); }

    //Public Methods
    public void scroll(double x, double y){
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);
    }

    public void changeZoomLevel(int zoomChange){
        int zoom = Math2.clamp(6, this.zoom.get() + zoomChange, 19);
        int offset = zoom - this.zoom.get();

        scroll(Math.pow(2, offset) * minX.get(), Math.pow(2, offset) * minY.get());
        this.zoom.set(zoom);
    }

}
