package edu.usf.cutr.gtfsrtvalidator.lib.test.util;

import edu.usf.cutr.gtfsrtvalidator.lib.util.CalendarUtils;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.text.ParseException;
import java.util.Collections;

import static org.junit.Assert.*;

public class CalendarUtilsTest {
    @Test
    public void testServiceIsAvailableIfSpecifiedInServiceCalendarDates() throws ParseException {
        ServiceCalendarDate serviceCalendarDate = new ServiceCalendarDate();
        serviceCalendarDate.setDate(ServiceDate.parseString("20190101"));
        serviceCalendarDate.setExceptionType(1);
        serviceCalendarDate.setServiceId(new AgencyAndId("test", "1"));

        CalendarUtils calendarUtils = new CalendarUtils(Collections.emptyList(), Collections.singletonList(serviceCalendarDate));

        assertTrue(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20190101")));
    }

    @Test
    public void testServiceIsNotAvailableIfSpecifiedInServiceCalendarDates() throws ParseException {
        ServiceCalendarDate serviceCalendarDate = new ServiceCalendarDate();
        serviceCalendarDate.setDate(ServiceDate.parseString("20190101"));
        serviceCalendarDate.setExceptionType(2);
        serviceCalendarDate.setServiceId(new AgencyAndId("test", "1"));

        CalendarUtils calendarUtils = new CalendarUtils(Collections.emptyList(), Collections.singletonList(serviceCalendarDate));

        assertFalse(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20190101")));
    }

    @Test
    public void testWeekdayServiceIsAvailableOnWeekdays() throws ParseException {
        ServiceCalendar serviceCalendar = new ServiceCalendar();
        serviceCalendar.setMonday(1);
        serviceCalendar.setTuesday(1);
        serviceCalendar.setWednesday(1);
        serviceCalendar.setThursday(1);
        serviceCalendar.setFriday(1);
        serviceCalendar.setSaturday(0);
        serviceCalendar.setSunday(0);
        serviceCalendar.setServiceId(new AgencyAndId("test", "1"));
        serviceCalendar.setStartDate(ServiceDate.parseString("20181201"));
        serviceCalendar.setEndDate(ServiceDate.parseString("20190201"));

        CalendarUtils calendarUtils = new CalendarUtils(Collections.singletonList(serviceCalendar), Collections.emptyList());

        assertTrue(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20190101")));
    }

    @Test
    public void testWeekendServiceIsNotAvailableOnWeekdays() throws ParseException {
        ServiceCalendar serviceCalendar = new ServiceCalendar();
        serviceCalendar.setMonday(0);
        serviceCalendar.setTuesday(0);
        serviceCalendar.setWednesday(0);
        serviceCalendar.setThursday(0);
        serviceCalendar.setFriday(0);
        serviceCalendar.setSaturday(1);
        serviceCalendar.setSunday(1);
        serviceCalendar.setServiceId(new AgencyAndId("test", "1"));
        serviceCalendar.setStartDate(ServiceDate.parseString("20181201"));
        serviceCalendar.setEndDate(ServiceDate.parseString("20190201"));

        CalendarUtils calendarUtils = new CalendarUtils(Collections.singletonList(serviceCalendar), Collections.emptyList());

        assertFalse(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20190101")));
    }

    @Test
    public void testServiceIsNotAvailableOutsideCalendarRange() throws ParseException {
        ServiceCalendar serviceCalendar = new ServiceCalendar();
        serviceCalendar.setMonday(0);
        serviceCalendar.setTuesday(0);
        serviceCalendar.setWednesday(0);
        serviceCalendar.setThursday(0);
        serviceCalendar.setFriday(0);
        serviceCalendar.setSaturday(1);
        serviceCalendar.setSunday(1);
        serviceCalendar.setServiceId(new AgencyAndId("test", "1"));
        serviceCalendar.setStartDate(ServiceDate.parseString("20181201"));
        serviceCalendar.setEndDate(ServiceDate.parseString("20190201"));

        CalendarUtils calendarUtils = new CalendarUtils(Collections.singletonList(serviceCalendar), Collections.emptyList());

        assertFalse(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20190401")));
        assertFalse(calendarUtils.isServiceAvailableOnDate("1", ServiceDate.parseString("20180401")));
    }
}
