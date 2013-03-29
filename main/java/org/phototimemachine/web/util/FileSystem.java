package org.phototimemachine.web.util;

import java.io.File;

public class FileSystem {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String PATH = OS.indexOf("win") >= 0 ?
            "C:\\works\\data\\data\\" :
            "/data/";

    public static void removePhoto(Long id) {
        try {
            File directory = new File(PATH + id);
            for (File item: directory.listFiles())
                item.delete();
            directory.delete();
        } catch (Exception ex) { }
    }
}
