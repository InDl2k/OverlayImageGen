package App;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImageBuilder {

    private Map<String, File[]> layerFiles;
    private Map<String, File> layerFile;
    private Map<String, Integer> layerPriority;
    private Set<String> directoryNames;

    ImageBuilder(){
        layerFiles = new HashMap<>();
        layerFile = new HashMap<>();
        layerPriority = new HashMap<>();
        directoryNames = new HashSet<>();
    }

    public void clear(){
        layerFiles.clear();
        layerPriority.clear();
        directoryNames.clear();
        layerFile.clear();
    }

    public Set<String> getDirectoryNames() {
        return directoryNames;
    }

    public Integer getLayerPriority(String nameLayer){
        return layerPriority.get(nameLayer);
    }

    public void setLayerPriority(String nameLayer, Integer priorityLayer){
        if(nameLayer != null)
            layerPriority.put(nameLayer, priorityLayer);
    }

    public void getData(File[] directories){
        for (var directory : directories) {
            File[] files = directory.listFiles();

            File[] pngs = FileChecker.getPngs(files);
            if(pngs != null) {
                layerFiles.put(directory.getName(), pngs);
                layerPriority.put(directory.getName(), 1);
                directoryNames.add(directory.getName());
            }
            else{
                System.out.printf("No png files in dir %s\n", directory.getName());
            }
        }
    }

    public void generateImage(){
        for(Map.Entry<String, File[]> entry : layerFiles.entrySet()){
            File img = entry.getValue()[(int)(Math.random() * (entry.getValue().length-1))];
            layerFile.put(entry.getKey(), img);
        }
    }

    private ArrayList<Pair<Integer, File>> getImage(){
        ArrayList<Pair<Integer, File>> imgArr = new ArrayList<>();
        for(Map.Entry<String, File> entry : layerFile.entrySet()){
            imgArr.add(new Pair(layerPriority.get(entry.getKey()), entry.getValue()));
        }
        Collections.sort(imgArr, comp);
        return imgArr;
    }

    public Image build(int width, int height){
        ArrayList<Pair<Integer, File>> imgArr = getImage();

        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = res.createGraphics();

        try {
            for(var priorityFilePair : imgArr){
                BufferedImage temp = ImageIO.read(priorityFilePair.getValue());
                g.drawImage(temp, 0, 0, width, height, null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return SwingFXUtils.toFXImage(res, null);
    }

    public void save(Image img){
        String date = new SimpleDateFormat("yyyy HH-mm-ss.SSSZ", new Locale("ru", "RU")).format(new Date());
        String filename = date + ".png";

        String dir = System.getProperty("user.dir") + "\\res"; new File(dir).mkdir();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "PNG", new File(System.getProperty("user.dir") + "\\res\\" +  filename));
        }
        catch (IOException exc){
            System.out.println(exc.getMessage());
        }
    }

    Comparator comp = new Comparator<Pair<Integer, File[]>>() {
        @Override
        public int compare(Pair<Integer, File[]> o1, Pair<Integer, File[]> o2) {
            if (o1.getKey() <= o2.getKey())
                return 1;
            else if (o1.getKey().equals(o2.getKey()))
                return 0;
            else
                return -1;
        }
    };

}
