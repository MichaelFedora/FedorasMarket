package io.github.michaelfedora.fedorasmarket.database;

/**
 * Created by Michael on 3/12/2016.
 */
public class BadDataException extends Exception {
    public BadDataException () {

    }

    public BadDataException (String message) {
        super (message);
    }

    public BadDataException (Throwable cause) {
        super (cause);
    }

    public BadDataException (String message, Throwable cause) {
        super (message, cause);
    }
}
