package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Class representing a tile manager
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class TileManager {

    private final LinkedHashMap<TileId, Image> cacheMemory;
    private final Path cachePath;
    private final String serverName;

    //* Constants

    private static final String HTTPS = "https://";
    private static final String SLASH = "/";
    private static final String PNG = ".png";
    private static final String JAVIONS = "Javions";
    private static final String USER_AGENT = "User-Agent";
    private static final int CACHE_MEMORY_CAPACITY = 100;
    private static final float CACHE_MEMORY_LOAD_FACTOR = 0.75f;

    //* Constructor

    /**
     * TileManager's constructor
     * @param cachePath the cache path
     * @param serverName the server name
     */
    public TileManager(Path cachePath, String serverName){
        cacheMemory = new LinkedHashMap<>(CACHE_MEMORY_CAPACITY, CACHE_MEMORY_LOAD_FACTOR,true);
        this.cachePath = cachePath;
        this.serverName = serverName;
    }


    //* Methods

    /**
     * Gets the image corresponding to the tile at the tileId
     * @param tileId the tileId
     * @return the image for the tile at the tileId
     * @throws IOException if the image is null
     */
    public Image imageForTileAt(TileId tileId) throws IOException{
        String pathAnnex = SLASH + tileId.zoom() + SLASH + tileId.coordX() + SLASH;
        Path imageDiskPath = Path.of(cachePath.toString(), pathAnnex + tileId.coordY() + PNG);
        Image image;

        if(cacheMemory.get(tileId) != null){
            return cacheMemory.get(tileId);
        } else if (Files.exists(imageDiskPath)) {

            InputStream i = new FileInputStream(imageDiskPath.toString());
            image = new Image(i);
            i.close();

            if (cacheMemory.size() >= CACHE_MEMORY_CAPACITY){
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            }
            cacheMemory.put(tileId, image);

            return image;
        }else{
            Path imageServerPath = Path.of(serverName + pathAnnex + tileId.coordY() + PNG);
            Path docPath = Path.of(cachePath + pathAnnex);
            byte[] tab;

            URL url = new URL(HTTPS + imageServerPath);
            URLConnection c = url.openConnection();
            c.setRequestProperty(USER_AGENT, JAVIONS);

            InputStream i1 = c.getInputStream();
            tab = i1.readAllBytes();
            i1.close();
            Files.createDirectories(docPath);
            Files.createFile(imageDiskPath);

            FileOutputStream o1 = new FileOutputStream(imageDiskPath.toString());
            ByteArrayInputStream i2 = new ByteArrayInputStream(tab);
            o1.write(tab);
            o1.close();
            image = new Image(i2);
            i2.close();

            if(cacheMemory.size() >= CACHE_MEMORY_CAPACITY){
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            }
            cacheMemory.put(tileId, image);

            return image;
        }
    }

    //* Record

    /**
     * Record representing a tileId
     */
    public record TileId(int zoom, int coordX, int coordY) {
       
        /**
         * Checks if the tileId is valid
         * @param z the zoom
         * @param x the x coordinate
         * @param y the y coordinate
         * @return true if the tileId is valid, false otherwise
         */
        public static boolean isValid(int z, int x, int y){
            return (0 <= x && x <= (1<<z) && 0 <= y && y <= (1<<z));
        }
    }

}
