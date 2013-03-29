package org.phototimemachine.web.util.user;

public class UserParser {

    public static int isEmailOrUsername(String value) {
        // Search username
        if (value.indexOf(" ") >= 0) return 0;
        // Search by email
        else if (value.indexOf("@") >= 0) return 1;
        else return -1;
    }

    public static String[] getCorrectUsername(String value) {
        String[] elements = value.split(" ");
        if (elements.length > 1 && elements[0].length() > 0 && elements[1].length() > 0)
            return elements;
        else
            return null;
    }

    public static final int USER_EMAIL = 1;
    public static final int USER_NAME  = 0;
    public static final int USER_FAIL  = -1;
}
