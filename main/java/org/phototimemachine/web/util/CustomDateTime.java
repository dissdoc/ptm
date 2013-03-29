package org.phototimemachine.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateTime {

    private DateFormat format;

    public CustomDateTime() {
        format = new SimpleDateFormat("MM-dd-yyyy");
    }

    public Date getDate(String day, String month, String year) {
        int _day = Numerical.getInt(day);
        int _month = Numerical.getInt(month);
        int _year = Numerical.getInt(year);

        if (_day == 0 && _month == 0 && _year == 0) return null;

        try {
            return format.parse(_month+"-"+_day+"-"+_year);
        } catch (Exception ex) {
            return null;
        }
    }


}
