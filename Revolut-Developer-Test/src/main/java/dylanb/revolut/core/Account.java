package dylanb.revolut.core;

import java.math.BigDecimal;

import dylanb.revolut.exception.NegativeBalanceException;

public class Account {

	private Long accountID;
	private BigDecimal balance;

	public Account(Long accountID, BigDecimal balance) {
		this.accountID = accountID;
		this.setBalance(balance);
	}

	public Long getAccountID() {
		return accountID;
	}

	public void setAccountID(long accountID) {
		this.accountID = accountID;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance == null ? BigDecimal.ZERO : balance;
	}

	public void addToBalance(final BigDecimal valueToAdd) {
		balance = balance.add(valueToAdd);
	}

	public void removeFromBalance(final BigDecimal valueToRemove) throws NegativeBalanceException {
		if (valueToRemove.compareTo(balance) == 1) {
			throw new NegativeBalanceException(
					"Removing " + valueToRemove + " from Account " + accountID + " will result in negative balance");
		}
		balance = balance.subtract(valueToRemove);
	}
}