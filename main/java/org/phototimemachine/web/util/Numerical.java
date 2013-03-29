package org.phototimemachine.web.util;

public class Numerical {

    public static int getInt(String param) {
        try {
            return Integer.parseInt(param);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static byte getByte(String param) {
        try {
            return Byte.parseByte(param);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static Boolean getBool(String param) {
        try {
            if (param != null && param.equals("1")) return Boolean.TRUE;
            else return Boolean.FALSE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    public static Double getDouble(String param) {
        try {
            return new Double(param);
        } catch (Exception ex) { return new Double(0.0); }
    }
}
