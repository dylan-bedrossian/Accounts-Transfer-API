package dylanb.revolut.exception;

public final class ExistingAccountException extends Exception {
    public ExistingAccountException(final String message) {
        super(message);
    }
}
