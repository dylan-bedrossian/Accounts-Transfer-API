package dylanb.revolut.exception;

public final class NegativeBalanceException extends Exception {
    public NegativeBalanceException(final String message) {
        super(message);
    }
}
