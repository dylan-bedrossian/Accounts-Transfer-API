package dylanb.revolut.db;

import dylanb.revolut.core.Account;
import dylanb.revolut.core.Transaction;
import dylanb.revolut.exception.ExistingAccountException;
import dylanb.revolut.exception.InvalidTransactionException;
import dylanb.revolut.exception.NegativeBalanceException;
import dylanb.revolut.exception.NegativeTransactionAmountException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AccountsDao {

	private List<Account> accounts;

	public AccountsDao() {
		this.accounts = new ArrayList<Account>();
	}

	public AccountsDao(List<Account> accounts) {
		this.accounts = accounts;
	}

	public Account getAccount(long accountID) {
		for (Account acct : accounts) {
			if (acct.getAccountID() == accountID) {
				return acct;
			}
		}
		throw new NoSuchElementException("The account " + accountID + " does not exist.");
	}

	public void addAccount(Account account) throws ExistingAccountException {
		for (Account acct : accounts) {
			if (acct.getAccountID().equals(account.getAccountID())) {
				throw new ExistingAccountException("Cannot re-add existing account " + account.getAccountID());
			}
		}
		accounts.add(account);
	}

	public static String getValueAsString(Object obj) {
		return obj == null ? "null" : String.valueOf(obj);
	}

	public void validateTransaction(Transaction transaction)
			throws InvalidTransactionException, NegativeTransactionAmountException {
		String fromAccStr = getValueAsString(transaction.getFromAccount());
		String toAccStr = getValueAsString(transaction.getToAccount());
		String amountStr = getValueAsString(transaction.getAmount());

		if (fromAccStr == "null" || toAccStr == "null" || amountStr == "null") {
			throw new InvalidTransactionException("Cannot execute with invalid transaction (From account: " + fromAccStr
					+ "; To Account: " + toAccStr + "; Amount: " + amountStr + ")");
		} else if (transaction.getAmount().doubleValue() < 0.00) {
			throw new NegativeTransactionAmountException(
					"Cannot execute transaction with a negative amount: " + transaction.getAmount());
		}
	}

	public void executeTransaction(Transaction transaction)
			throws InvalidTransactionException, NegativeBalanceException, NegativeTransactionAmountException {
		validateTransaction(transaction);
		Account fromAccount = getAccount(transaction.getFromAccount());
		Account toAccount = getAccount(transaction.getToAccount());
		fromAccount.removeFromBalance(transaction.getAmount());
		toAccount.addToBalance(transaction.getAmount());
	}
}
