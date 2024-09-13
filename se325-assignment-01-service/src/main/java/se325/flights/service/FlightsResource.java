package se325.flights.service;

import se325.flights.domain.Airport;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A JAX-RS Resource class for retrieving information about particular flights.
 */
public class FlightsResource {

    /**
     * Parses the given departure date query. If the query matches the format "YYYY-MM-DD" (e.g. "2021-08-16"), an array
     * of two {@link ZonedDateTime} instances corresponding to 00:00:00 and 23:59:59 on the given date in the given
     * timezone is returned. If dayRange is > 0, the range is expanded by (24 * dayRange) hours on either side.
     *
     * @param departureDateQuery the date / time query to parse
     * @param dayRange           the range, in days. Adds (24 * dayRange) hours on each side of the range to search.
     * @param timezone           the timezone to parse. Should come from {@link Airport#getTimeZone()}
     * @return an array of two {@link ZonedDateTime} instances, representing the beginning and end of the given date
     * in the given timezone
     * @throws DateTimeException if departureDateQuery or timezone are invalid
     */
    private ZonedDateTime[] parseDepartureDateQuery(String departureDateQuery, int dayRange, String timezone) throws DateTimeException {
        LocalDate departureDate = LocalDate.parse(departureDateQuery, DateTimeFormatter.ISO_DATE);

        // TODO This method doesn't consider the dayRange argument yet. Modify it so that it does.

        return new ZonedDateTime[]{
                ZonedDateTime.of(departureDate, LocalTime.MIN, ZoneId.of(timezone)),
                ZonedDateTime.of(departureDate, LocalTime.MAX, ZoneId.of(timezone))
        };
    }

}
