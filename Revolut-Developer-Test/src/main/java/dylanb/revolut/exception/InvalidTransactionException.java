package dylanb.revolut.exception;

public final class InvalidTransactionException extends Exception {
    public InvalidTransactionException(final String message) {
        super(message);
    }
}
