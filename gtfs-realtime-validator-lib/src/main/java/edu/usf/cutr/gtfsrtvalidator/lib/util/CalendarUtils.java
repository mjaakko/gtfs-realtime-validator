package edu.usf.cutr.gtfsrtvalidator.lib.util;

import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsDao;

import java.time.LocalDate;
import java.util.*;

public class CalendarUtils {
    //Map service id to service calendars
    private Map<String, List<ServiceCalendar>> serviceCalendars = new HashMap<>();
    //Map service id to service dates
    private Map<String, List<ServiceCalendarDate>> serviceCalendarDates = new HashMap<>();

    public CalendarUtils(GtfsDao gtfsDao) {
        this(gtfsDao.getAllCalendars(), gtfsDao.getAllCalendarDates());
    }

    public CalendarUtils(Collection<ServiceCalendar> serviceCalendars, Collection<ServiceCalendarDate> serviceCalendarDates) {
        serviceCalendars.forEach(serviceCalendar -> this.serviceCalendars.compute(serviceCalendar.getServiceId().getId(), (id, list) -> {
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(serviceCalendar);
            return list;
        }));
        serviceCalendarDates.forEach(serviceCalendarDate -> this.serviceCalendarDates.compute(serviceCalendarDate.getServiceId().getId(), (id, list) -> {
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(serviceCalendarDate);
            return list;
        }));
    }

    public boolean isServiceAvailableOnDate(String serviceId, ServiceDate date) {
        for (ServiceCalendarDate serviceCalendarDate : serviceCalendarDates.getOrDefault(serviceId, Collections.emptyList())) {
            //If exception type is 2, service has been removed and is not available for the date regardless of other rules
            if (serviceCalendarDate.getDate().equals(date) && serviceCalendarDate.getExceptionType() == 2) {
                return false;
            //If exception type is 1, service has been added and is available for the date regardless of other rules
            } else if (serviceCalendarDate.getDate().equals(date) && serviceCalendarDate.getExceptionType() == 1) {
                return true;
            }
        }

        return serviceCalendars.getOrDefault(serviceId, Collections.emptyList()).stream().anyMatch(serviceCalendar -> {
            if (serviceCalendar.getStartDate().compareTo(date) <= 0 && serviceCalendar.getEndDate().compareTo(date) >= 0) {
                LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());

                switch (localDate.getDayOfWeek()) {
                    case MONDAY:
                        return serviceCalendar.getMonday() == 1;
                    case TUESDAY:
                        return serviceCalendar.getTuesday() == 1;
                    case WEDNESDAY:
                        return serviceCalendar.getWednesday() == 1;
                    case THURSDAY:
                        return serviceCalendar.getThursday() == 1;
                    case FRIDAY:
                        return serviceCalendar.getFriday() == 1;
                    case SATURDAY:
                        return serviceCalendar.getSaturday() == 1;
                    case SUNDAY:
                        return serviceCalendar.getSunday() == 1;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        });
    }
}
