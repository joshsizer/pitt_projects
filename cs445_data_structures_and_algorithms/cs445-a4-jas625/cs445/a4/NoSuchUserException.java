package cs445.a4;

/**
 * Indicates that a user does not exist
 */
public class NoSuchUserException extends Exception {
    public NoSuchUserException(String message) {
        super(message);
    }
}
