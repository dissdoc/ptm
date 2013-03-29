package org.phototimemachine.web.util;

/**
 * Created with IntelliJ IDEA.
 * User: kmuhov
 * Date: 22.03.13
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public class FormatDate {

    public static String dt(int day, int month, int year) {
            StringBuilder builder = new StringBuilder();

            if (year <= 0) return null;
            builder.append(year);

            if (month <= 0) return builder.toString();
            builder.insert(0, months[month] + ", ");

            if (day <= 0) return builder.toString();
            builder.insert(0, day+" ");
            return builder.toString();
    }

    public static String fulldt(String start, String end) {
        String fullDate = "";
        if (start != null && start.length() > 1)
            fullDate += start;

        if (start != null && end != null && start.length() > 1 && end.length() > 1)
            fullDate += " - ";

        if (end != null && end.length() > 1)
            fullDate += end;
        return fullDate;
    }

    public static final String[] months = new String[] {
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
}
