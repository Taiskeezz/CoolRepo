package se325.flights.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import se325.flights.domain.User;

import javax.persistence.EntityManager;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Contains methods for generating SHA3-256 hashes and random UUID strings
 */
public class SecurityUtils {

    private static final ThreadLocal<DigestUtils> THREAD_LOCAL_DIGEST =
            ThreadLocal.withInitial(() -> new DigestUtils("SHA3-256"));

    /**
     * Generates a SHA3-256 hash of the given password string, and returns it as a hex string.
     *
     * @param password the password to hash
     * @return the hex-string representation of the SHA3-256 hash of the provided value
     */
    public static String getSHA256Hash(String password) {
        return THREAD_LOCAL_DIGEST.get().digestAsHex(password);
    }

    /**
     * Returns a value indicating whether the given hash is the SHA3-256 hash of the given password.
     *
     * @param password the password to check
     * @param hash     the hash to check
     * @return true if there is a match, false otherwise
     */
    public static boolean matches(String password, String hash) {
        return getSHA256Hash(password).equals(hash);
    }

    /**
     * Generates a random UUID, as a String. Can be used as an auth token.
     *
     * @return a random UUID string.
     */
    public static String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates a {@link NewCookie} instance suitable for an authentication token.
     *
     * @param username the username of the authenticated user
     * @param uuid     a random string which should also be saved to the user's "uuid" field in the database.
     */
    public static NewCookie generateAuthCookie(String username, String uuid) {

        String jwt = createJWT(username, uuid);

        return new NewCookie(
                "authToken",
                jwt,
                "/",
                null,
                null,
                604800 * 2, // About two weeks
                false
        );
    }

    /**
     * Creates a {@link NewCookie} instance suitable for deleting the authentication token.
     */
    public static NewCookie generateDeleteAuthCookie() {
        return new NewCookie("authToken",
                null,
                "/",
                null,
                null,
                0,
                false);
    }

    /**
     * Creates a new JWT (JSON Web Token) with the provided values for the "username" and "uuid" claims.
     * Used by {@link #generateAuthCookie(String, String)} above.
     *
     * @param username the username claim value
     * @param uuid     the uuid claim value
     * @return a valid JWT which can be, for example, added to an auth cookie.
     */
    public static String createJWT(String username, String uuid) {
        Algorithm algo = Algorithm.HMAC256("SE325A12024S2");
        return JWT.create()
                .withClaim("username", username)
                .withClaim("uuid", uuid)
                .sign(algo);
    }

    /**
     * Decodes the given encoded JWT.
     *
     * @param token the encoded JWT to decode
     * @return the decoded JWT
     * @throws JWTVerificationException if encoded JWT is invalid, has been tampered with, or could
     *                                  otherwise not be verified.
     */
    public static DecodedJWT decodeJWT(String token) throws JWTVerificationException {
        Algorithm algo = Algorithm.HMAC256("SE325A12024S2");
        JWTVerifier verifier = JWT.require(algo).build();
        return verifier.verify(token);
    }

    /**
     * Gets the {@link User} object with the given auth cookie. The cookie's value should be a valid JWT which was
     * originally created with the {@link #createJWT(String, String)} method above. It will be decoded with the
     * {@link #decodeJWT(String)} method, and its "username" and "uuid" claims checked against users in the
     * database.
     *
     * @param em         the entity manager to use to execute the database query
     * @param authCookie the auth cookie to check
     * @return the {@link User} whose username and uuid in the database match the username and uuid claims in
     * the cookie's valid JWT.
     * @throws NotAuthorizedException if there's no such user or the cookie doesn't contain a valid JWT.
     */
    public static User getUserWithAuthCookie(EntityManager em, Cookie authCookie) throws NotAuthorizedException {

        // Check for non-null auth cookie
        if (authCookie == null) {
            em.getTransaction().rollback();
            throw new NotAuthorizedException(Response.status(401, "Not authenticated").build());
        }

        // Pull out JWT from cookie and decode
        String encodedJWT = authCookie.getValue();
        DecodedJWT decodedJWT;
        try {
            decodedJWT = decodeJWT(encodedJWT);
        } catch (JWTVerificationException e) {
            throw new NotAuthorizedException(Response.status(401, "Not authenticated").build());
        }

        try {

            // Pull out username and uuid from JWT
            String username = decodedJWT.getClaim("username").asString();
            String uuid = decodedJWT.getClaim("uuid").asString();

            return em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.uuid = :uuid", User.class)
                    .setParameter("username", username)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new NotAuthorizedException(Response.status(401, "Not authenticated").build());
        }
    }

}
