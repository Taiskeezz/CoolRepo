package se325.flights.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user of the system. Users are capable of authenticating and making {@link FlightBooking}s on
 * {@link Flight}s.
 */
public class User {

    private Long id;

    private String username;
    private String passHash;
    private String uuid;

    private Set<FlightBooking> bookings = new HashSet<>();

    /**
     * Default constructor, required by JPA
     */
    public User() {
    }

    /**
     * Creates a new User object
     *
     * @param username the username
     * @param passHash the SHA3-256 hash of the user's password
     */
    public User(String username, String passHash) {
        this.username = username;
        this.passHash = passHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Set<FlightBooking> getBookings() {
        return bookings;
    }
}
