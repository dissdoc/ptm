package org.phototimemachine.web.util.image;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.process.ProcessStarter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class CustomImageMagick {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String PATH = OS.indexOf("win") >= 0 ?
            "C:\\works\\data\\data\\" :
            "/data/";
    private static final String AVATAR_PATH = OS.indexOf("win") >= 0 ?
            "C:\\works\\data\\avatar\\" :
            "/avatar/";

    private static String ORIGINAL = "original";
    private static String MEDIUM   = "medium";
    private static String SHOWING  = "showing";
    private static String WALL     = "wall";
    private static String ICON     = "icon";

    private String folder;
    private String format;
    private String path;
    private MultipartFile file;

    public CustomImageMagick(MultipartFile file, String folder) {
        if (OS.indexOf("win") >= 0) {
            String myPath="C:\\Program Files\\ImageMagick-6.8.3-Q16";
            ProcessStarter.setGlobalSearchPath(myPath);
        }

        this.file = file;
        this.folder = folder;
        this.format = null;
        this.path = null;
    }

    public CustomImageMagick() {
        this.file = null;
        this.folder = null;
        this.format = null;
        this.path = null;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setFormat() {
        if (format != null) return;

        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        if (index <= 0) {
            format = null;
            return;
        }
        format = fileName.substring(index, fileName.length());
    }

    public void setPath() {
        if (path != null) return;

        path = PATH + folder;
        File dir = new File(path);
        dir.mkdir();
        path += System.getProperty("file.separator");
    }

    public void setAvatarPath() {
        if (path != null) return;

        path = AVATAR_PATH + folder;
        File dir = new File(path);
        dir.mkdir();
        path += System.getProperty("file.separator");
    }

    public void generate() {
        if (format == null) return;

        int param = ImageFormatter.format(format);
        if (param == -1) return;

        ConvertCmd cmd = new ConvertCmd();

        saveOriginalFile(file, path, format);
        try {
            String name = path + ORIGINAL + format;
            Info info = new Info(name, true);
            int height = info.getImageHeight();
            int width = info.getImageWidth();

            cmd.run(saveMediumFile());
            cmd.run(saveShowingFile(width, height));
            cmd.run(saveWallFile(width, height));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void generate2(int w, int x, int y, int _w, int _h) {
        if (format == null) return;

        int param = ImageFormatter.format(format);
        if (param == -1) return;

        ConvertCmd cmd = new ConvertCmd();

        saveOriginalFile(file, path, format);

        try {
            String name = path + ORIGINAL + format;
            Info info = new Info(name, true);
            int height = info.getImageHeight();
            int width = info.getImageWidth();
            cmd.run(mediumAvatar(height, width, w, x, y, _w, _h));
            cmd.run(showingAvatar(width, height));
            cmd.run(wallAvatar(width, height));
            cmd.run(iconAvatar());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private IMOperation iconAvatar() {
        IMOperation imop = new IMOperation();
        imop.addImage(path + SHOWING + ".png");
        imop.resize(64, 64);
        imop.addImage(path + ICON + ".png");
        return imop;
    }

    private IMOperation mediumAvatar(int height, int width, int w, int x, int y, int _w, int _h) {
        int translate_x = width * x / _w;
        int translate_y = height * y / _h;
        int originalW = width * w / _w;

        IMOperation imop = new IMOperation();
        imop.addImage(path + ORIGINAL + format);
        imop.crop(originalW, originalW, translate_x, translate_y);
        imop.addImage(path + MEDIUM + ".png");
        return imop;
    }

    private IMOperation showingAvatar(int w, int h) {
        int crop_x = 0, crop_y = 0, releaseW = 250, releaseH = 164;

        int _W = 250;
        int _H = 250 * h / w;
        if (_H > 164) {
            releaseH = 0;
            releaseW = 250;
            crop_y = (_H - 164) / 2;
        } else if (_H < 164) {
            _W = 164 * w / h;
            releaseW = 0;
            releaseH = 164;
            crop_x = (_W - 250) / 2;
        }

        IMOperation imop = new IMOperation();
        imop.addImage(path + ORIGINAL + format);
        imop.resize(releaseW == 0 ? null: releaseW, releaseH == 0 ? null: releaseH);
        imop.crop(250, 164, crop_x, crop_y);
        imop.addImage(path + SHOWING + ".png");
        return imop;
    }

    private IMOperation wallAvatar(int w, int h) {
        int crop_x = 0, crop_y = 0, releaseW = 166, releaseH = 110;

        int _W = 166;
        int _H = 166 * h / w;
        if (_H > 110) {
            releaseH = 0;
            releaseW = 166;
            crop_y = (_H - 110) / 2;
        } else if (_H < 110) {
            _W = 110 * w / h;
            releaseW = 0;
            releaseH = 110;
            crop_x = (_W - 166) / 2;
        }

        IMOperation imop = new IMOperation();
        imop.addImage(path + SHOWING + ".png");
        imop.resize(releaseW == 0 ? null: releaseW, releaseH == 0 ? null: releaseH);
        imop.crop(166, 110, crop_x, crop_y);
        imop.addImage(path + WALL + ".png");
        return imop;
    }

    private IMOperation saveShowingFile(int w, int h) {
        int crop_x = 0, crop_y = 0, releaseW = 250, releaseH = 164;

        int _W = 250;
        int _H = 250 * h / w;
        if (_H > 164) {
            releaseH = 0;
            releaseW = 250;
            crop_y = (_H - 164) / 2;
        } else if (_H < 164) {
            _W = 164 * w / h;
            releaseW = 0;
            releaseH = 164;
            crop_x = (_W - 250) / 2;
        }

        IMOperation imop = new IMOperation();
        imop.addImage(path + MEDIUM + ".png");
        imop.resize(releaseW == 0 ? null: releaseW, releaseH == 0 ? null: releaseH);
        imop.crop(250, 164, crop_x, crop_y);
        imop.addImage(path + SHOWING + ".png");
        return imop;
    }

    private IMOperation saveWallFile(int w, int h) {
        int crop_x = 0, crop_y = 0, releaseW = 166, releaseH = 110;

        int _W = 166;
        int _H = 166 * h / w;
        if (_H > 110) {
            releaseH = 0;
            releaseW = 166;
            crop_y = (_H - 110) / 2;
        } else if (_H < 110) {
            _W = 110 * w / h;
            releaseW = 0;
            releaseH = 110;
            crop_x = (_W - 166) / 2;
        }

        IMOperation imop = new IMOperation();
        imop.addImage(path + SHOWING + ".png");
        imop.resize(releaseW == 0 ? null: releaseW, releaseH == 0 ? null: releaseH);
        imop.crop(166, 110, crop_x, crop_y);
        imop.addImage(path + WALL + ".png");
        return imop;
    }

    private IMOperation saveMediumFile() {
        IMOperation imop = new IMOperation();
        imop.addImage(path + ORIGINAL + format);
        imop.adaptiveResize(820, 620);
        imop.addImage(path + MEDIUM + ".png");
        return imop;
    }

    private static void saveOriginalFile(MultipartFile file, String path, String format) {
        try {
            file.transferTo(new File(path + ORIGINAL + format));
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}