package App;

import java.io.File;
import java.util.ArrayList;

public class FileChecker {

    public static File[] getPngs(File[] files){
        ArrayList<File> dirs = new ArrayList<>(); //find all .png
        for(var file : files){
            if(file.getName().length() >= 4 && (file.getName().substring(file.getName().length() - 4).equals(".png"))){
                dirs.add(file);
            }
        }
        File[] pngs = new File[dirs.size()];
        pngs = dirs.toArray(pngs);
        if(pngs.length > 0) {
            return pngs;
        }
        else {
            return null;
        }
    }
}
