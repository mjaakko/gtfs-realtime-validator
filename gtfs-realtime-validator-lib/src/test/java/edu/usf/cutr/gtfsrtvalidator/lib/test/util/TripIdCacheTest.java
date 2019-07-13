package edu.usf.cutr.gtfsrtvalidator.lib.test.util;

import edu.usf.cutr.gtfsrtvalidator.lib.util.CalendarUtils;
import edu.usf.cutr.gtfsrtvalidator.lib.util.TripIdCache;
import org.junit.Before;
import org.junit.Test;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TripIdCacheTest {
    private TripIdCache tripIdCache;

    @Before
    public void setup() throws ParseException {
        ServiceCalendar serviceCalendar = new ServiceCalendar();
        serviceCalendar.setStartDate(ServiceDate.parseString("20000101"));
        serviceCalendar.setEndDate(ServiceDate.parseString("21000101"));
        serviceCalendar.setMonday(1);
        serviceCalendar.setTuesday(1);
        serviceCalendar.setWednesday(1);
        serviceCalendar.setThursday(1);
        serviceCalendar.setFriday(1);
        serviceCalendar.setSaturday(1);
        serviceCalendar.setSunday(1);
        serviceCalendar.setServiceId(new AgencyAndId("test", "1"));

        ServiceCalendarDate serviceCalendarDate = new ServiceCalendarDate();
        serviceCalendarDate.setServiceId(new AgencyAndId("test", "1"));
        serviceCalendarDate.setExceptionType(2);
        serviceCalendarDate.setDate(ServiceDate.parseString("20200101"));

        Route route1 = new Route();
        route1.setId(new AgencyAndId("test", "10"));

        Trip trip1 = new Trip();
        trip1.setRoute(route1);
        trip1.setId(new AgencyAndId("test", "trip_1"));
        trip1.setDirectionId("1");
        trip1.setServiceId(new AgencyAndId("test", "1"));

        Trip trip2 = new Trip();
        trip2.setRoute(route1);
        trip2.setId(new AgencyAndId("test", "trip_2"));
        trip2.setDirectionId("0");
        trip2.setServiceId(new AgencyAndId("test", "1"));

        Trip trip3 = new Trip();
        trip3.setRoute(route1);
        trip3.setId(new AgencyAndId("test", "trip_3"));
        trip3.setServiceId(new AgencyAndId("test", "1"));

        Map<String, List<StopTime>> stopTimes = new HashMap<>();

        StopTime stopTime1 = new StopTime();
        stopTime1.setStopSequence(0);
        stopTime1.setTrip(trip1);
        stopTime1.setDepartureTime(6 * 60 * 60); // 6.00AM

        StopTime stopTime2 = new StopTime();
        stopTime2.setStopSequence(1);
        stopTime2.setTrip(trip1);
        stopTime2.setArrivalTime(6 * 60 * 60 + 2 * 60); // 6.20AM

        stopTimes.put("trip_1", Arrays.asList(stopTime1, stopTime2));

        StopTime stopTime3 = new StopTime();
        stopTime3.setStopSequence(0);
        stopTime3.setTrip(trip2);
        stopTime3.setDepartureTime(6 * 60 * 60); // 6.00AM

        StopTime stopTime4 = new StopTime();
        stopTime4.setStopSequence(1);
        stopTime4.setTrip(trip2);
        stopTime4.setArrivalTime(6 * 60 * 60 + 2 * 60); // 6.20AM

        stopTimes.put("trip_2", Arrays.asList(stopTime3, stopTime4));

        StopTime stopTime5 = new StopTime();
        stopTime5.setStopSequence(0);
        stopTime5.setTrip(trip3);
        stopTime5.setDepartureTime(7 * 60 * 60); // 7.00AM

        StopTime stopTime6 = new StopTime();
        stopTime6.setStopSequence(1);
        stopTime6.setTrip(trip3);
        stopTime6.setArrivalTime(7 * 60 * 60 + 2 * 60); // 7.20AM

        stopTimes.put("trip_3", Arrays.asList(stopTime5, stopTime6));

        tripIdCache = new TripIdCache(
                Arrays.asList(trip1, trip2, trip3),
                stopTimes,
                new CalendarUtils(Collections.singletonList(serviceCalendar), Collections.singletonList(serviceCalendarDate)));
    }

    @Test
    public void testCanFindTripIdToDirection1() {
        assertEquals("trip_1", tripIdCache.findTripId("10", "20100101", "06:00:00", "1"));
    }

    @Test
    public void testCanFindTripIdToDirection0() {
        assertEquals("trip_2", tripIdCache.findTripId("10", "20100101", "06:00:00", "0"));
    }

    @Test
    public void testCanFindTripIdWithoutDirectionIdWhenDepartureTimeIsUnique() {
        assertEquals("trip_3", tripIdCache.findTripId("10", "20100101", "07:00:00", null));
    }

    @Test
    public void testCanNotFindTripIdOnDateWhenTripIsNotAvailable() {
        assertNull(tripIdCache.findTripId("10", "20200101", "06:00:00", "1"));
    }
}
