package se325.flights.service.test;

import se325.flights.dto.BookingInfoDTO;
import se325.flights.dto.FlightDTO;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functionality related to retrieving flight information.
 */
public class FlightIT extends BaseIntegrationTests {

    /**
     * Tests that the correct flight info is returned when we search for flights by airport name. The
     * response should be a 200 OK with a list of valid {@link se325.flights.dto.FlightDTO} objects. The list should
     * contain 4 flights, sorted by departure time ascending.
     */
    @Test
    public void testFlightSearch_AirportsByName() {
        try (Response response = clientRequest("/flights?origin=Auckland&destination=Sydney").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("ZNJ-242"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("WJF-883"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("ZWZ-576"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("YLJ-355"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the correct flight info is returned when we search for flights by airport code. The
     * response should be a 200 OK with a list of valid {@link se325.flights.dto.FlightDTO} objects. The list should
     * contain 4 flights, sorted by departure time ascending.
     */
    @Test
    public void testFlightSearch_AirportsByCode() {
        try (Response response = clientRequest("/flights?origin=AKL&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("ZNJ-242"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("WJF-883"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("ZWZ-576"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("YLJ-355"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the flight origin and destination search (by name) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByName_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=AUCKLanD&destination=SyDney").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("ZNJ-242"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("WJF-883"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("ZWZ-576"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("YLJ-355"), responseFlights.get(3));
        }
    }

    /**
     * Tests that the flight origin and destination search (by code) is case-insensitive.
     */
    @Test
    public void testFlightSearch_AirportsByCode_CaseInsensitive() {
        try (Response response = clientRequest("/flights?origin=aKL&destination=sYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(4, responseFlights.size());

            assertEquals(FLIGHTS.get("ZNJ-242"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("WJF-883"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("ZWZ-576"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("YLJ-355"), responseFlights.get(3));
        }
    }

    /**
     * Tests that a flight search can get results from multiple matching origins, in the correct order.
     * "ng" matches los aNGeles and siNGapore. This search should return 8 results.
     */
    @Test
    public void testFlightSearch_MultipleMatchingOrigins() {
        try (Response response = clientRequest("/flights?origin=ng&destination=SYD").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(8, responseFlights.size());

            assertEquals(FLIGHTS.get("AWJ-994"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("SJY-964"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("MTU-228"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("ZPV-405"), responseFlights.get(3));
            assertEquals(FLIGHTS.get("VBR-241"), responseFlights.get(4));
            assertEquals(FLIGHTS.get("FPZ-912"), responseFlights.get(5));
            assertEquals(FLIGHTS.get("FHF-294"), responseFlights.get(6));
            assertEquals(FLIGHTS.get("YAJ-395"), responseFlights.get(7));
        }
    }

    /**
     * Tests that a flight search can get results from multiple matching destinations, in the correct order.
     * "ng" matches los aNGeles and siNGapore. This search should return 8 results.
     */
    @Test
    public void testFlightSearch_MultipleMatchingDestinations() {
        try (Response response = clientRequest("/flights?origin=AKL&destination=ng").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(8, responseFlights.size());

            assertEquals(FLIGHTS.get("ZZO-347"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("IEE-697"), responseFlights.get(1));
            assertEquals(FLIGHTS.get("FEX-930"), responseFlights.get(2));
            assertEquals(FLIGHTS.get("PWK-304"), responseFlights.get(3));
            assertEquals(FLIGHTS.get("DDE-510"), responseFlights.get(4));
            assertEquals(FLIGHTS.get("JQB-702"), responseFlights.get(5));
            assertEquals(FLIGHTS.get("YFJ-842"), responseFlights.get(6));
            assertEquals(FLIGHTS.get("VRT-298"), responseFlights.get(7));
        }
    }

    /**
     * Tests that a flight search with no results still returns a 200 OK response, just with an empty list.
     */
    @Test
    public void testFlightSearch_NoResults() {
        try (Response response = clientRequest("/flights?origin=foobar&destination=AKL").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> flights = response.readEntity(new GenericType<>() {
            });
            assertEquals(0, flights.size());
        }
    }

    /**
     * Tests that a flight search with a missing origin will return a 400 response
     */
    @Test
    public void testFlightSearchFail_MissingOrigin() {
        try (Response response = clientRequest("/flights?destination=Sydney").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a missing destination will return a 400 response
     */
    @Test
    public void testFlightSearchWithMissingDestination() {
        try (Response response = clientRequest("/flights?origin=Auckland").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with a departure time query will return results within 8 days of the given
     * date, in the origin's time zone, in departure date order.
     */
    @Test
    public void testFlightSearchWithDepartureTime() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2022-08-21&dayRange=8").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            List<FlightDTO> responseFlights = response.readEntity(new GenericType<>() {
            });
            assertEquals(2, responseFlights.size());
            assertEquals(FLIGHTS.get("WJF-883"), responseFlights.get(0));
            assertEquals(FLIGHTS.get("ZWZ-576"), responseFlights.get(1));
        }
    }

    /**
     * Tests that a flight search with an invalid departure time query will return a 400 Bad Request error.
     */
    @Test
    public void testFlightSearchWithDepartureTimeFail_InvalidDate() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=invalid").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that a flight search with an invalid day range query will return a 400 Bad Request error.
     */
    @Test
    public void testFlightSearchWithDepartureTimeFail_InvalidDayRange() {
        try (Response response = clientRequest(
                "/flights?origin=AKL&destination=SYD&departureDate=2022-08-21&dayRange=-1").get()) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    /**
     * Tests that we can get booking info for a flight which exists
     */
    @Test
    public void testRetrieveBookingInfo() {
        try (Response response = clientRequest("/flights/43/booking-info").get()) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            BookingInfoDTO info = response.readEntity(BookingInfoDTO.class);

            assertEquals("787-9 Dreamliner", info.getAircraftType().getName());
            assertEquals(0, info.getBookedSeats().size());
        }
    }

    /**
     * Tests that we get a 404 error for requesting booking info for a nonexistent flight
     */
    @Test
    public void testRetrieveBookingInfoFail_NotFound() {
        try (Response response = clientRequest("/flights/999/booking-info").get()) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }
}
