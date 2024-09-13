package se325.flights.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a booking by a particular {@link User} on a particular {@link Flight}.
 */
public class FlightBooking {

    private Long id;
    private Flight flight;
    private User user;
    private Set<Seat> seats = new HashSet<>();

    /**
     * Default constructor, required by JPA / Hibernate
     */
    public FlightBooking() {
    }

    /**
     * Creates a new FlightBooking
     *
     * @param user   the user making the booking
     * @param flight the flight being booked
     */
    public FlightBooking(User user, Flight flight) {
        this.user = user;
        this.flight = flight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    protected void setFlight(Flight flight) {
        this.flight = flight;
    }

    public User getUser() {
        return user;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    /**
     * Gets the price of this booking. The price is calculated by summing the price of all {@link Seat}s in this
     * booking (using their {@link Seat#getPrice()} method).
     */
    public int getPrice() {
        return this.seats.stream()
                .map(Seat::getPrice)
                .reduce(0, Integer::sum);
    }
}
