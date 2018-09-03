package com.groupstp.cifra.web.tasks.task;

import com.groupstp.cifra.entity.tasks.IntervalType;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

public class Utils {


    /**
     * @param startDate
     * @param interval     Integer
     * @param intervalType Hour,Day,Month....
     * @return - startDate + interval of intervalType (example: 01.01.2018 + 3 of Day = 04.01.2018)
     */
    public static Date countEndDateFromStartDate(Date startDate, Integer interval, IntervalType intervalType) {
        switch (intervalType) {
            case Days:
                return DateUtils.addDays(startDate, interval);
            case Weeks:
                return DateUtils.addWeeks(startDate, interval);
            case Months:
                return DateUtils.addMonths(startDate, interval);
        }
        return null;
    }
}
