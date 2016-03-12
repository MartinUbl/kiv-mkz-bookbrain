package cz.paranoid.mobile.bookbrain.misc;

import java.util.Calendar;
import java.util.Date;

public class DateWorker
{
    private static class HolidayContainer
    {
        public int month;
        public int day;

        public HolidayContainer(int day, int month)
        {
            this.month = month;
            this.day = day;
        }
    }

    private static HolidayContainer[] holidays = {
            new HolidayContainer(1, 1),
            new HolidayContainer(1, 5),
            new HolidayContainer(8, 5),
            new HolidayContainer(5, 7),
            new HolidayContainer(6, 7),
            new HolidayContainer(28, 9),
            new HolidayContainer(28, 10),
            new HolidayContainer(17, 11),
            new HolidayContainer(24, 12),
            new HolidayContainer(25, 12),
            new HolidayContainer(26, 12)
    };

    public static Calendar moveByWorkingDays(Calendar cal, int dayCount)
    {
        boolean moveDay = false;

        for (int i = 0; i < dayCount; i++)
        {
            moveDay = false;
            cal.add(Calendar.DATE, 1);

            // add one more day for every holiday day found
            for (int j = 0; j < holidays.length; j++)
            {
                if (cal.get(cal.DAY_OF_MONTH) == holidays[j].day && cal.get(cal.MONTH) == holidays[j].month)
                    moveDay = true;
            }

            // saturdays and sundays
            if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7)
                moveDay = true;

            if (moveDay)
                i--;
        }

        return cal;
    }

    public static Calendar moveToNextWorkingDay(Calendar cal)
    {
        boolean moveDay = false;

        do
        {
            moveDay = false;

            // add one more day for every holiday day found
            for (int j = 0; j < holidays.length; j++)
            {
                if (cal.get(cal.DAY_OF_MONTH) == holidays[j].day && cal.get(cal.MONTH)+1 == holidays[j].month)
                    moveDay = true;
            }

            // saturdays and sundays
            if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7)
                moveDay = true;

            if (moveDay)
                cal.add(Calendar.DATE, 1);
        }
        while (moveDay);

        return cal;
    }
}
