package com.car.castel.BookingService.utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MyDateUtils extends org.apache.commons.lang.time.DateUtils {


    /**
     * Check "x"  range and "y" date range is intersected
     *
     * @param xFrom
     * @param xTo
     * @param yFrom
     * @param yTo
     * @return
     */
    public static boolean isXBetweenY (Date xFrom,Date xTo, Date yFrom,Date yTo){
        boolean b = (xFrom.getTime() >= yFrom.getTime() && xFrom.getTime() <= yTo.getTime()) ||
                (xTo.getTime() >= yFrom.getTime() && xTo.getTime() <= yTo.getTime());
        return b;
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {

        long diffInMillis = date2.getTime() - date1.getTime();

        //create the list
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        //create the result map of TimeUnit and difference
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long millisRest = diffInMillis;

        for ( TimeUnit unit : units ) {

            //calculate difference in millisecond
            long diff = unit.convert(millisRest,TimeUnit.MILLISECONDS);
            long diffInMillisForUnit = unit.toMillis(diff);
            millisRest = millisRest - diffInMillisForUnit;

            //put the result in the map
            result.put(unit,diff);
        }

        return result;
    }

    /**
     *
     * @param date given date
     * @param hours to add + value, to subtract - value
     * @return modified date
     */
    public static Date addOrSubtractHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * CHeck whether the given data is with in the given range from start-end
     *
     * @param testDate date which is testing
     * @param startDate
     * @param endDate
     * @return result
     */
    public static boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }


}
