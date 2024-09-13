package se325.flights.service;

import se325.flights.dto.AvailableSeatsSubscriptionDTO;

import javax.persistence.EntityManager;
import javax.ws.rs.container.AsyncResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A central place to hold all the subscriptions and subscription management / processing methods. Singleton so that
 * any resource instance can access the same subscription service.
 */
public class SubscriptionManager {

    private static SubscriptionManager instance;

    public static SubscriptionManager instance() {
        if (instance == null) {
            instance = new SubscriptionManager();
        }
        return instance;
    }

    private SubscriptionManager() {
    }

    /**
     * In charge of executing subscription processing
     */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    /**
     * Holds all subscriptions
     */
    private final Map<AvailableSeatsSubscriptionDTO, AsyncResponse> subs = new HashMap<>();

    /**
     * Adds the given subscription to the collection of ongoing subs.
     *
     * @param subInfo info about the flight for which seats are required, the number of required seats, the required
     *                cabin class of those seats (if any), and the user who made the request
     * @param sub     the {@link AsyncResponse} object that will eventually be used to send the response back to the
     *                client
     */
    public void addSubscription(AvailableSeatsSubscriptionDTO subInfo, AsyncResponse sub) {
        this.subs.put(subInfo, sub);
    }

    /**
     * Runs through all subscriptions pertaining to the flight with the given id, and processes them. This is done on a
     * different Thread.
     *
     * @param flightId the id of the flight whose subs should be processed
     */
    public void processSubscriptions(final long flightId) {
        threadPool.submit(() -> {
            final EntityManager em = PersistenceManager.instance().createEntityManager();
            try {

                // Find all subscriptions for the given flight
                List<Map.Entry<AvailableSeatsSubscriptionDTO, AsyncResponse>> subsToProcess = subs.entrySet().stream()
                        .filter(entry -> flightId == entry.getKey().getFlightId())
                        .collect(Collectors.toList());

                // For each of those subscriptions, process them. If they were successfully processed, remove them
                // from the collection, so they won't be processed again.
                subsToProcess.forEach(entry -> {
                    if (processSingleSubscription(entry.getKey(), entry.getValue(), em)) {
                        subs.remove(entry.getKey());
                    }
                });
            } finally {
                em.close();
            }
        });
    }

    /**
     * Processes a single subscription. Search for that subscription's flight and check how many seats it has of the
     * given cabin class.
     * <p>
     * If the flight doesn't exist, resumes the AsyncResponse with a 404 response, and returns true
     * <p>
     * If the flight contains at least the required number of unbooked seats of the required cabin class (or any cabin
     * class if the provided class is null), resumes the AsyncResponse with a 204 No Content response, and returns true
     * <p>
     * Otherwise, returns false.
     *
     * @param subInfo the subscription to process
     * @param sub     the {@link AsyncResponse} to use to send a response back to the client
     * @param em      the {@link EntityManager} to use to access the database
     * @return true if the sub was successfully processed (i.e. {@link AsyncResponse#resume} was called),
     * false otherwise.
     */
    public boolean processSingleSubscription(AvailableSeatsSubscriptionDTO subInfo, AsyncResponse sub, EntityManager em) {

        // TODO: Implement this method
        return false;
    }
}
