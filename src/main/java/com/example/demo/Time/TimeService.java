package com.example.demo.Time;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TimeService {

    public String currentDateFormat() {
        // Get the current date and time formatted as "yyyy-MM-dd HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentDate = LocalDateTime.now().format(formatter);
        return currentDate;
    }

    // This method can be used to parse the date in your specific format
    public Date SdfTime(String runTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String scheduledDateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + runTime;
        Date scheduledTime = sdf.parse(scheduledDateTime);
        return scheduledTime;
    }

    // New method to parse the specific date format you're working with
    public Date parseCustomDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        return sdf.parse(dateString);
    }

    // Method to add one day to a date and return it as a String in the same format
    public Date addOneDay(Date date, int update) {
        long oneDayInMillis = TimeUnit.DAYS.toMillis(update); // Convert x day to milliseconds
        return new Date(date.getTime() + oneDayInMillis);  // Add x day to the date
    }
}
