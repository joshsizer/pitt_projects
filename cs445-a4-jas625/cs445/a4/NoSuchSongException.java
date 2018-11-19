package cs445.a4;

/**
 * Indicates that a song does not exist
 */
public class NoSuchSongException extends Exception {
    public NoSuchSongException(String message) {
        super(message);
    }
}
