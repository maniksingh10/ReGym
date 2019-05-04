package com.veersgym.manik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    public static String millsToDateFormat(long mills) {

        Date date = new Date(mills);
        DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        String dateFormatted = formatter.format(date);
        return dateFormatted; //note that it will give you the time in GMT+0
    }


}
