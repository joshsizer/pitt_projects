package cs445.a4;

/**
 * Indicates that a station does not exist
 */
public class NoSuchStationException extends Exception {
    public NoSuchStationException(String message) {
        super(message);
    }
}
