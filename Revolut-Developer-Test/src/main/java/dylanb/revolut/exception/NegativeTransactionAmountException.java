package dylanb.revolut.exception;

public final class NegativeTransactionAmountException extends Exception {
    public NegativeTransactionAmountException(final String message) {
        super(message);
    }
}
