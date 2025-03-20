package com.example.demo.Time;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TimeUtilService {



    // Parses a given time string (HH:mm) into a Date object for today's date
    public Date parseTimeForToday(String dateTimeString) throws ParseException {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            throw new ParseException("Input date string is empty", 0);
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate;

        try {
            parsedDate = inputFormat.parse(dateTimeString);
        } catch (ParseException e) {
            throw new ParseException("Unable to parse date: " + dateTimeString, e.getErrorOffset());
        }
        return parsedDate;
    }
    public String getOneDayFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Add 1 day
        Date oneDayFromNow = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(oneDayFromNow);
    }

    // This method parses a full date with the format you're dealing with, which includes day of the week, month, date, time, timezone, and year
    public Date parseCustomDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        return sdf.parse(dateString);
    }

    // Adds a given number of days to a date and returns the updated Date object
    public Date addDays(Date date, int days) {
        long daysInMillis = TimeUnit.DAYS.toMillis(days);
        return new Date(date.getTime() + daysInMillis);
    }


    // Format the Date into a custom format "EEE MMM dd HH:mm:ss z yyyy"
    public String formatToCustomDateFormat(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        return outputFormat.format(date);
    }
    }