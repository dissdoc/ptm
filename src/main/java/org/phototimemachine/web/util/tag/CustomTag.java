package org.phototimemachine.web.util.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomTag {

    public static List<String> getTagList(String source) {
        if (source == null || source.trim().length() <= 0) return new ArrayList<String>();
        String[] items = source.split(",\\s*");
        return Arrays.asList(items);
    }
}
