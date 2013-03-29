package org.phototimemachine.web.util.image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageFormatter {

    private static final String IMAGE_NO_TIFF_PATTERN = "((\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
    private static final String IMAGE_TIFF_PATTERN = "((\\.(?i)(tiff|tif))$)";

    private static Pattern patternNoTiff = Pattern.compile(IMAGE_NO_TIFF_PATTERN);
    private static Pattern patternTiff = Pattern.compile(IMAGE_TIFF_PATTERN);

    /**
     * Initiation image format with regular expression
     * @param image image for validation
     * @return -1 not image, 0 image no tiff, 1 image tiff
     */
    public static int format(final String image) {
        Matcher matcher = patternNoTiff.matcher(image);
        if (matcher.matches()) return 0;

        matcher = patternTiff.matcher(image);
        if (matcher.matches()) return 1;

        return -1;
    }
}
