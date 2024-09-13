package se325.flights.service.test;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import se325.flights.dto.AirportDTO;
import se325.flights.dto.BookingRequestDTO;
import se325.flights.dto.FlightDTO;
import se325.flights.dto.UserDTO;
import se325.flights.util.SecurityUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Code that's common to all Integration Tests.
 */
public abstract class BaseIntegrationTests {

    protected static final Map<String, AirportDTO> AIRPORTS = new HashMap<>();
    protected static final Map<String, FlightDTO> FLIGHTS = new HashMap<>();

    /** The following airports and flights are auto-generated. */
    static {
        AIRPORTS.put("AKL", new AirportDTO(1L, "Auckland International Airport", "AKL", -37.008, 174.792, "Pacific/Auckland"));
        AIRPORTS.put("SYD", new AirportDTO(2L, "Sydney International Airport", "SYD", -33.946, 151.177, "Australia/Sydney"));
        AIRPORTS.put("NRT", new AirportDTO(3L, "Tokyo Narita International Airport", "NRT", 35.765, 140.386, "Asia/Tokyo"));
        AIRPORTS.put("SIN", new AirportDTO(4L, "Singapore Changi International Airport", "SIN", 1.356, 103.987, "Asia/Singapore"));
        AIRPORTS.put("LAX", new AirportDTO(5L, "Los Angeles International Airport", "LAX", 33.942, -118.408, "America/Los_Angeles"));

        FLIGHTS.put("ZNJ-242", new FlightDTO(1L, "ZNJ-242", time("2022-08-11T13:00:00"), AIRPORTS.get("AKL"), time("2022-08-11T16:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("WJF-883", new FlightDTO(2L, "WJF-883", time("2022-08-23T19:00:00"), AIRPORTS.get("AKL"), time("2022-08-23T22:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("ZWZ-576", new FlightDTO(3L, "ZWZ-576", time("2022-08-29T07:00:00"), AIRPORTS.get("AKL"), time("2022-08-29T10:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("YLJ-355", new FlightDTO(4L, "YLJ-355", time("2022-08-31T04:00:00"), AIRPORTS.get("AKL"), time("2022-08-31T07:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("BLG-598", new FlightDTO(5L, "BLG-598", time("2022-08-29T07:00:00"), AIRPORTS.get("AKL"), time("2022-08-29T18:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("VID-108", new FlightDTO(6L, "VID-108", time("2022-08-30T02:00:00"), AIRPORTS.get("AKL"), time("2022-08-30T13:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("IMQ-765", new FlightDTO(7L, "IMQ-765", time("2022-08-26T23:00:00"), AIRPORTS.get("AKL"), time("2022-08-27T10:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("VRT-298", new FlightDTO(8L, "VRT-298", time("2022-08-25T04:00:00"), AIRPORTS.get("AKL"), time("2022-08-25T15:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("JQB-702", new FlightDTO(9L, "JQB-702", time("2022-08-16T17:00:00"), AIRPORTS.get("AKL"), time("2022-08-17T04:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("FEX-930", new FlightDTO(10L, "FEX-930", time("2022-08-11T05:00:00"), AIRPORTS.get("AKL"), time("2022-08-11T16:00:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("IEE-697", new FlightDTO(11L, "IEE-697", time("2022-08-11T03:00:00"), AIRPORTS.get("AKL"), time("2022-08-11T14:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("PWK-304", new FlightDTO(12L, "PWK-304", time("2022-08-16T12:00:00"), AIRPORTS.get("AKL"), time("2022-08-17T01:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("YFJ-842", new FlightDTO(13L, "YFJ-842", time("2022-08-20T04:00:00"), AIRPORTS.get("AKL"), time("2022-08-20T17:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("DDE-510", new FlightDTO(14L, "DDE-510", time("2022-08-16T12:00:00"), AIRPORTS.get("AKL"), time("2022-08-17T01:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("ZZO-347", new FlightDTO(15L, "ZZO-347", time("2022-08-09T13:00:00"), AIRPORTS.get("AKL"), time("2022-08-10T02:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("QQE-292", new FlightDTO(16L, "QQE-292", time("2022-08-31T01:00:00"), AIRPORTS.get("SYD"), time("2022-08-31T04:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("BBB-122", new FlightDTO(17L, "BBB-122", time("2022-08-15T08:00:00"), AIRPORTS.get("SYD"), time("2022-08-15T11:10:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("OWX-760", new FlightDTO(18L, "OWX-760", time("2022-08-27T12:00:00"), AIRPORTS.get("SYD"), time("2022-08-27T15:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("ZWE-876", new FlightDTO(19L, "ZWE-876", time("2022-09-04T05:00:00"), AIRPORTS.get("SYD"), time("2022-09-04T08:10:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("DQL-372", new FlightDTO(20L, "DQL-372", time("2022-08-11T09:00:00"), AIRPORTS.get("SYD"), time("2022-08-11T19:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("UQL-438", new FlightDTO(21L, "UQL-438", time("2022-08-11T14:00:00"), AIRPORTS.get("SYD"), time("2022-08-12T00:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("RXU-158", new FlightDTO(22L, "RXU-158", time("2022-09-07T00:00:00"), AIRPORTS.get("SYD"), time("2022-09-07T10:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("EYF-144", new FlightDTO(23L, "EYF-144", time("2022-09-07T11:00:00"), AIRPORTS.get("SYD"), time("2022-09-07T19:20:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("HCD-255", new FlightDTO(24L, "HCD-255", time("2022-09-05T04:00:00"), AIRPORTS.get("SYD"), time("2022-09-05T12:20:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("SED-857", new FlightDTO(25L, "SED-857", time("2022-08-15T23:00:00"), AIRPORTS.get("SYD"), time("2022-08-16T07:20:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("QAA-364", new FlightDTO(26L, "QAA-364", time("2022-08-17T09:00:00"), AIRPORTS.get("SYD"), time("2022-08-18T00:30:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("FOS-262", new FlightDTO(27L, "FOS-262", time("2022-09-07T11:00:00"), AIRPORTS.get("SYD"), time("2022-09-08T02:30:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("NLX-575", new FlightDTO(28L, "NLX-575", time("2022-08-19T15:00:00"), AIRPORTS.get("SYD"), time("2022-08-20T06:30:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("IDD-802", new FlightDTO(29L, "IDD-802", time("2022-09-01T14:00:00"), AIRPORTS.get("NRT"), time("2022-09-02T01:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("YHQ-591", new FlightDTO(30L, "YHQ-591", time("2022-08-22T04:00:00"), AIRPORTS.get("NRT"), time("2022-08-22T15:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("CML-765", new FlightDTO(31L, "CML-765", time("2022-08-30T19:00:00"), AIRPORTS.get("NRT"), time("2022-08-31T06:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("GMX-889", new FlightDTO(32L, "GMX-889", time("2022-08-21T10:00:00"), AIRPORTS.get("NRT"), time("2022-08-21T21:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("GWQ-815", new FlightDTO(33L, "GWQ-815", time("2022-08-22T14:00:00"), AIRPORTS.get("NRT"), time("2022-08-23T00:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("VWM-185", new FlightDTO(34L, "VWM-185", time("2022-08-29T03:00:00"), AIRPORTS.get("NRT"), time("2022-08-29T13:10:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("OZL-258", new FlightDTO(35L, "OZL-258", time("2022-08-26T21:00:00"), AIRPORTS.get("NRT"), time("2022-08-27T07:10:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("QFT-111", new FlightDTO(36L, "QFT-111", time("2022-08-15T19:00:00"), AIRPORTS.get("NRT"), time("2022-08-16T02:10:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("DPX-900", new FlightDTO(37L, "DPX-900", time("2022-08-26T15:00:00"), AIRPORTS.get("NRT"), time("2022-08-26T22:10:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("AVE-146", new FlightDTO(38L, "AVE-146", time("2022-09-02T02:00:00"), AIRPORTS.get("NRT"), time("2022-09-02T09:10:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("ZJX-309", new FlightDTO(39L, "ZJX-309", time("2022-08-09T23:00:00"), AIRPORTS.get("NRT"), time("2022-08-10T06:10:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("ZFD-241", new FlightDTO(40L, "ZFD-241", time("2022-08-20T08:00:00"), AIRPORTS.get("NRT"), time("2022-08-20T19:30:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("ETN-391", new FlightDTO(41L, "ETN-391", time("2022-08-27T14:00:00"), AIRPORTS.get("NRT"), time("2022-08-28T01:30:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("LKE-071", new FlightDTO(42L, "LKE-071", time("2022-08-10T17:00:00"), AIRPORTS.get("NRT"), time("2022-08-11T04:30:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("YJY-087", new FlightDTO(43L, "YJY-087", time("2022-09-01T11:00:00"), AIRPORTS.get("SIN"), time("2022-09-01T22:00:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("NAK-343", new FlightDTO(44L, "NAK-343", time("2022-08-12T14:00:00"), AIRPORTS.get("SIN"), time("2022-08-13T01:00:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("MMY-188", new FlightDTO(45L, "MMY-188", time("2022-08-31T13:00:00"), AIRPORTS.get("SIN"), time("2022-09-01T00:00:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("VBR-241", new FlightDTO(46L, "VBR-241", time("2022-08-18T13:00:00"), AIRPORTS.get("SIN"), time("2022-08-18T21:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("MTU-228", new FlightDTO(47L, "MTU-228", time("2022-08-16T12:00:00"), AIRPORTS.get("SIN"), time("2022-08-16T20:20:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("SJY-964", new FlightDTO(48L, "SJY-964", time("2022-08-10T12:00:00"), AIRPORTS.get("SIN"), time("2022-08-10T20:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("FPZ-912", new FlightDTO(49L, "FPZ-912", time("2022-08-19T06:00:00"), AIRPORTS.get("SIN"), time("2022-08-19T14:20:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("FBH-075", new FlightDTO(50L, "FBH-075", time("2022-09-06T08:00:00"), AIRPORTS.get("SIN"), time("2022-09-06T15:10:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("ESE-011", new FlightDTO(51L, "ESE-011", time("2022-08-16T22:00:00"), AIRPORTS.get("SIN"), time("2022-08-17T05:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("OCI-638", new FlightDTO(52L, "OCI-638", time("2022-08-31T16:00:00"), AIRPORTS.get("SIN"), time("2022-08-31T23:10:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("IFT-689", new FlightDTO(53L, "IFT-689", time("2022-08-15T10:00:00"), AIRPORTS.get("SIN"), time("2022-08-16T04:00:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("SQP-422", new FlightDTO(54L, "SQP-422", time("2022-08-26T13:00:00"), AIRPORTS.get("SIN"), time("2022-08-27T07:00:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("SUF-575", new FlightDTO(55L, "SUF-575", time("2022-08-08T22:00:00"), AIRPORTS.get("SIN"), time("2022-08-09T16:00:00"), AIRPORTS.get("LAX"), "787-9 Dreamliner"));
        FLIGHTS.put("UND-319", new FlightDTO(56L, "UND-319", time("2022-08-25T18:00:00"), AIRPORTS.get("SIN"), time("2022-08-26T12:00:00"), AIRPORTS.get("LAX"), "777-200ER"));
        FLIGHTS.put("ADR-346", new FlightDTO(57L, "ADR-346", time("2022-08-24T22:00:00"), AIRPORTS.get("LAX"), time("2022-08-25T11:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("HPR-130", new FlightDTO(58L, "HPR-130", time("2022-08-12T18:00:00"), AIRPORTS.get("LAX"), time("2022-08-13T07:30:00"), AIRPORTS.get("AKL"), "777-200ER"));
        FLIGHTS.put("UKC-561", new FlightDTO(59L, "UKC-561", time("2022-08-21T03:00:00"), AIRPORTS.get("LAX"), time("2022-08-21T16:30:00"), AIRPORTS.get("AKL"), "787-9 Dreamliner"));
        FLIGHTS.put("ZPV-405", new FlightDTO(60L, "ZPV-405", time("2022-08-16T23:00:00"), AIRPORTS.get("LAX"), time("2022-08-17T14:30:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("AWJ-994", new FlightDTO(61L, "AWJ-994", time("2022-08-09T23:00:00"), AIRPORTS.get("LAX"), time("2022-08-10T14:30:00"), AIRPORTS.get("SYD"), "787-9 Dreamliner"));
        FLIGHTS.put("FHF-294", new FlightDTO(62L, "FHF-294", time("2022-08-19T20:00:00"), AIRPORTS.get("LAX"), time("2022-08-20T11:30:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("YAJ-395", new FlightDTO(63L, "YAJ-395", time("2022-08-28T09:00:00"), AIRPORTS.get("LAX"), time("2022-08-29T00:30:00"), AIRPORTS.get("SYD"), "777-200ER"));
        FLIGHTS.put("KML-365", new FlightDTO(64L, "KML-365", time("2022-08-10T03:00:00"), AIRPORTS.get("LAX"), time("2022-08-10T14:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("JNA-242", new FlightDTO(65L, "JNA-242", time("2022-08-19T14:00:00"), AIRPORTS.get("LAX"), time("2022-08-20T01:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("IJQ-029", new FlightDTO(66L, "IJQ-029", time("2022-08-28T05:00:00"), AIRPORTS.get("LAX"), time("2022-08-28T16:30:00"), AIRPORTS.get("NRT"), "787-9 Dreamliner"));
        FLIGHTS.put("CBM-270", new FlightDTO(67L, "CBM-270", time("2022-09-04T13:00:00"), AIRPORTS.get("LAX"), time("2022-09-05T00:30:00"), AIRPORTS.get("NRT"), "777-200ER"));
        FLIGHTS.put("VWR-623", new FlightDTO(68L, "VWR-623", time("2022-08-25T14:00:00"), AIRPORTS.get("LAX"), time("2022-08-26T08:00:00"), AIRPORTS.get("SIN"), "777-200ER"));
        FLIGHTS.put("KHS-671", new FlightDTO(69L, "KHS-671", time("2022-08-16T01:00:00"), AIRPORTS.get("LAX"), time("2022-08-16T19:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
        FLIGHTS.put("YFB-019", new FlightDTO(70L, "YFB-019", time("2022-08-17T19:00:00"), AIRPORTS.get("LAX"), time("2022-08-18T13:00:00"), AIRPORTS.get("SIN"), "787-9 Dreamliner"));
    }

    protected static final ZonedDateTime time(String t) {
        return ZonedDateTime.parse(t + "+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    protected static final String WEB_SERVICE_URI = "http://localhost:10000/services";

    protected Client client;

    /**
     * Runs before each unit test to create the web service client, and send a test request which will force-re-init
     * the database. This ensures each unit test is starting with a clean playing field.
     */
    @BeforeEach
    public void setUp() {
        client = ClientBuilder.newClient();

        Response response = clientRequest("/test/reset-db").delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * After each test, close the client to clear any leftover auth cookie data
     */
    @AfterEach
    public void tearDown() {
        client.close();
        client = null;
    }

    /**
     * Authorizes as user "Alice"
     */
    protected void logInAsAlice() {
        logInAs("Alice", "pa55word");
    }

    /**
     * Authorizes as user "Bob"
     */
    protected void logInAsBob() {
        logInAs("Bob", "12345");
    }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     */
    protected void logInAs(String username, String password) {
        logInAs(username, password, client);
    }

    /**
     * Authorizes as a user with the given username and password. Asserts that the given server response is a 204
     * No Content response, with an authToken cookie in the response header. Additionally, tests that the authToken
     * cookie's value is a valid JWT, with both username and uuid claims, whose username claim matches the provided
     * username.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @param client   the client to use to send the request
     */
    protected void logInAs(String username, String password, Client client) {
        UserDTO user = new UserDTO(username, password);
        try (Response response = clientRequest(client, "/users/login").post(Entity.json(user))) {

            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            var authCookie = response.getCookies().get("authToken");
            assertNotNull(authCookie);

            // Test JWT inside cookie
            String encodedJWT = authCookie.getValue();
            DecodedJWT jwt = SecurityUtils.decodeJWT(encodedJWT);
            assertNotNull(jwt.getClaim("username"));
            assertNotNull(jwt.getClaim("uuid"));
            assertEquals(username, jwt.getClaim("username").asString());
        }
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param path the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(String path) {
        return clientRequest(client, path);
    }

    /**
     * A shorthand for writing client.target(WEB_SERVICE_URI + path).request()...
     *
     * @param client the client to invoke
     * @param path   the path to append to {@link #WEB_SERVICE_URI}
     * @return the {@link Invocation.Builder} used to make a web request
     */
    protected Invocation.Builder clientRequest(Client client, String path) {
        return client.target(WEB_SERVICE_URI + path).request();
    }

    /**
     * Sends a request to the server to make a booking on the given flight, for the given seats. Checks the server
     * Response to make sure it is a 201 Created response, with a valid Location. Returns the URI pointing to the
     * booking.
     *
     * @param flightId the id of the flight to book
     * @param seats    the seats to book
     */
    protected URI makeBooking(long flightId, String... seats) {
        BookingRequestDTO request = new BookingRequestDTO(flightId, seats);
        try (Response response = clientRequest("/bookings").post(Entity.json(request))) {

            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull(response.getLocation());
            assertTrue(response.getLocation().toString().contains("/bookings/"));
            return response.getLocation();
        }
    }
}
