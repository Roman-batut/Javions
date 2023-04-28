package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {

    record TileId(int zoom, int coordX, int coordY) {
        public static boolean isValid(int z, int x, int y){
            return (0<=x && x<=(1<<z) && 0<=y && y<=(1<<z));
        }
    }

    private LinkedHashMap<TileId, Image> cacheMemory;
    private Path cachePath;
    private String serverName;

    public TileManager(Path cachePath, String serverName){
        cacheMemory = new LinkedHashMap<>(100,0.75f,true);
        this.cachePath = cachePath;
    }

    public Image imageForTileAt(TileId tileId)throws IOException{
        String pathAnnex = "/"+tileId.zoom()+"/"+tileId.coordX()+"/";
        Path imageDiskPath = Path.of(cachePath.toString(), pathAnnex, String.valueOf(tileId.coordY()), ".png" );
        Image image = null;
        if(cacheMemory.get(tileId) != null){
            return cacheMemory.get(tileId);
        } else if (Files.exists(imageDiskPath)) {
            try {
                URL u = null;
                u = new URL(imageDiskPath.toString());
                URLConnection c = null;
                c = u.openConnection();
                c.setRequestProperty("User-Agent", "Javions");
                InputStream i = c.getInputStream();
                image = new Image(i);
                i.close();
            }catch (IOException e) {
                System.out.println("Urlconnection fail or inputstream fail");
                throw e;
            }
            if(cacheMemory.size() == 100 ){
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            }
            cacheMemory.put(tileId, image);

            return image;
        }else{
            Path imageServerPath = Path.of(serverName, pathAnnex, String.valueOf(tileId.coordY()), ".png");
            Path docPath = Path.of(cachePath.toString(), pathAnnex);
            byte[] tab = null;
            try {
                URL url = new URL(HTTPS + imageServerPath);
                URLConnection c = url.openConnection();
                c.setRequestProperty("User-Agent", "Javions");

                InputStream i1 = c.getInputStream();
                tab = i1.readAllBytes();
                i1.close();
                Files.createDirectory(docPath);
                Files.createFile(imageDiskPath);
            } catch (IOException e) {
                System.out.println("Urlconnection fail or inputstream fail or filecreation fail");
                throw e;
            }
            try(FileOutputStream o1 = new FileOutputStream(imageDiskPath.toString())) {
                ByteArrayInputStream i2 = new ByteArrayInputStream(tab);
                o1.write(tab);
                o1.close();
                image = new Image(i2);
                i2.close();
            } catch (IOException e) {
                System.out.println("OutputStream write or creation fail or input streamfrombytefail");
                throw e;
            }
            if(cacheMemory.size() == 100 ){
                cacheMemory.remove(cacheMemory.keySet().iterator().next());
            }
            cacheMemory.put(tileId, image);
            return image;
        }
    }

}
// #TODO enlever les try and catch et laissé le throw IOexeption pour la gerer plus tard dans basemapcontroller


