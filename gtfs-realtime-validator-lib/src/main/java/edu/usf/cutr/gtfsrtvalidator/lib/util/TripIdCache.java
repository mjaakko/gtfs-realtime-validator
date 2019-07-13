package edu.usf.cutr.gtfsrtvalidator.lib.util;

import com.sun.org.apache.xpath.internal.operations.Gt;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.GtfsMetadata;
import org.hibernate.annotations.Sort;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.mappings.StopTimeFieldMappingFactory;
import org.onebusaway.gtfs.services.GtfsDao;
import org.onebusaway.gtfs.services.GtfsMutableDao;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class TripIdCache {
    private Map<String, String> tripIdCache = new HashMap<>();

    private Collection<Trip> trips;
    private Map<String, List<StopTime>> tripStopTimes;

    private CalendarUtils calendarUtils;

    public TripIdCache(Collection<Trip> trips, Map<String, List<StopTime>> tripStopTimes, CalendarUtils calendarUtils) {
        this.trips = trips;
        this.tripStopTimes = tripStopTimes;

        this.calendarUtils = calendarUtils;
    }

    /**
     * Finds a trip id for given route id, start date, start time and direction id. Direction id can be null if trip can be uniquely identified without it.
     * @param routeId Route ID
     * @param startDate Start date in format YYYYMMDD.
     * @param startTime Start time in format HH:MM:SS
     * @param directionId Direction id, either 0 or 1
     * @return Trip ID or null if no trip was found
     */
    public String findTripId(String routeId, String startDate, String startTime, String directionId) {
        return tripIdCache.computeIfAbsent(String.format("%s_%s_%s_%s", routeId, startDate, startTime, directionId), key -> {
            List<Trip> possibleTrips = trips.stream().filter(trip -> {
                Route route = trip.getRoute();

                if (route.getId().getId().equals(routeId)) {
                    try {
                        return calendarUtils.isServiceAvailableOnDate(trip.getServiceId().getId(), ServiceDate.parseString(startDate)) &&
                            doesTripStartAt(trip.getId().getId(), startTime);
                    } catch (ParseException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }).collect(Collectors.toList());

            if (possibleTrips.size() == 1) {
                return possibleTrips.get(0).getId().getId();
            } else {
                return possibleTrips.stream().filter(trip -> Objects.equals(trip.getDirectionId(), directionId)).findAny().map(trip -> trip.getId().getId()).orElse(null);
            }
        });
    }

    private boolean doesTripStartAt(String tripId, String startTime) {
        Optional<StopTime> firstStopTime = tripStopTimes.getOrDefault(tripId, Collections.emptyList()).stream().min(Comparator.comparingInt(StopTime::getStopSequence));
        return firstStopTime.filter(stopTime -> stopTime.isDepartureTimeSet() && stopTime.getDepartureTime() == StopTimeFieldMappingFactory.getStringAsSeconds(startTime)).isPresent();
    }
}
