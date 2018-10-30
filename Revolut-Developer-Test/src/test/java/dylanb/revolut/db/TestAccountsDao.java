package dylanb.revolut.db;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import dylanb.revolut.core.Account;
import dylanb.revolut.core.Transaction;
import dylanb.revolut.exception.ExistingAccountException;
import dylanb.revolut.exception.InvalidTransactionException;
import dylanb.revolut.exception.NegativeBalanceException;
import dylanb.revolut.exception.NegativeTransactionAmountException;

public class TestAccountsDao {

	private AccountsDao accountsDao = new AccountsDao();

	@Test
	public void test_AddAccount_OneNewAccount_DbSizeEqualsOne() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		Account account = mock(Account.class);

		accountsDao = new AccountsDao(accountDb);
		accountsDao.addAccount(account);
		assertEquals(accountDb.size(), 1);
	}

	@Test
	public void test_AddAccount_AddExistingAccount_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		Account existingAccount = mock(Account.class);
		Account newAccount = mock(Account.class);
		long accountID = 1234;

		when(existingAccount.getAccountID()).thenReturn(accountID);
		accountDb.add(existingAccount);
		accountsDao = new AccountsDao(accountDb);
		when(newAccount.getAccountID()).thenReturn(accountID);
		try {
			accountsDao.addAccount(newAccount);
			assert false;
		} catch (ExistingAccountException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_GetAccount_ExistingAccount_ReturnSameAccount() throws Exception {
		List<Account> accountsDB = new ArrayList<Account>();
		Account account = mock(Account.class);
		Long accountID = 123L;
		BigDecimal balance = new BigDecimal(123.23);

		when(account.getAccountID()).thenReturn(accountID);
		when(account.getBalance()).thenReturn(balance);
		accountsDB.add(account);
		accountsDao = new AccountsDao(accountsDB);

		Account actualAccount = accountsDao.getAccount(accountID);
		assertEquals(actualAccount.getAccountID(), accountID);
		assertEquals(actualAccount.getBalance(), balance);
	}

	@Test
	public void test_GetAccount_NonExistingAccount_ThrowsException() throws Exception {
		try {
			accountsDao.getAccount(1234);
			assert false;
		} catch (NoSuchElementException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_FromAccountNullID_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long toAccountID = 890;
		BigDecimal amount = BigDecimal.valueOf(12.34);
		Account fromAccount = mock(Account.class);
		Account toAccount = mock(Account.class);
		Transaction transaction = mock(Transaction.class);

		when(fromAccount.getAccountID()).thenReturn(null);
		when(toAccount.getAccountID()).thenReturn(toAccountID);

		when(transaction.getFromAccount()).thenReturn(null);
		when(transaction.getToAccount()).thenReturn(toAccountID);
		when(transaction.getAmount()).thenReturn(amount);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		try {
			accountsDao.executeTransaction(transaction);
			assert false;
		} catch (InvalidTransactionException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_ToAccountNullID_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long fromAccountID = 123;
		BigDecimal amount = BigDecimal.valueOf(12.34);
		Account fromAccount = mock(Account.class);
		Account toAccount = mock(Account.class);
		Transaction transaction = mock(Transaction.class);

		when(fromAccount.getAccountID()).thenReturn(fromAccountID);
		when(toAccount.getAccountID()).thenReturn(null);

		when(transaction.getFromAccount()).thenReturn(fromAccountID);
		when(transaction.getToAccount()).thenReturn(null);
		when(transaction.getAmount()).thenReturn(amount);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		try {
			accountsDao.executeTransaction(transaction);
			assert false;
		} catch (InvalidTransactionException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_AmountNull_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long fromAccountID = 123;
		long toAccountID = 890;
		Account fromAccount = mock(Account.class);
		Account toAccount = mock(Account.class);
		Transaction transaction = mock(Transaction.class);

		when(fromAccount.getAccountID()).thenReturn(fromAccountID);
		when(toAccount.getAccountID()).thenReturn(toAccountID);

		when(transaction.getFromAccount()).thenReturn(fromAccountID);
		when(transaction.getToAccount()).thenReturn(toAccountID);
		when(transaction.getAmount()).thenReturn(null);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		try {
			accountsDao.executeTransaction(transaction);
			assert false;
		} catch (InvalidTransactionException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_AmountNegative_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long fromAccountID = 123;
		long toAccountID = 890;
		BigDecimal amount = BigDecimal.valueOf(-12.34);
		Account fromAccount = mock(Account.class);
		Account toAccount = mock(Account.class);
		Transaction transaction = mock(Transaction.class);

		when(fromAccount.getAccountID()).thenReturn(fromAccountID);
		when(toAccount.getAccountID()).thenReturn(toAccountID);

		when(transaction.getFromAccount()).thenReturn(fromAccountID);
		when(transaction.getToAccount()).thenReturn(toAccountID);
		when(transaction.getAmount()).thenReturn(amount);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		try {
			accountsDao.executeTransaction(transaction);
			assert false;
		} catch (NegativeTransactionAmountException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_AmountLargerThanFromAccountBalance_ThrowsException() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long fromAccountID = 123;
		long toAccountID = 890;
		BigDecimal balanceFrom = BigDecimal.valueOf(100.00);
		BigDecimal amount = BigDecimal.valueOf(12333.34);
		Account fromAccount = new Account(fromAccountID, balanceFrom);
		Account toAccount = mock(Account.class);
		Transaction transaction = mock(Transaction.class);

		when(toAccount.getAccountID()).thenReturn(toAccountID);

		when(transaction.getFromAccount()).thenReturn(fromAccountID);
		when(transaction.getToAccount()).thenReturn(toAccountID);
		when(transaction.getAmount()).thenReturn(amount);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		try {
			accountsDao.executeTransaction(transaction);
			assert false;
		} catch (NegativeBalanceException e) {
			assert true;
		} catch (Exception e) {
			assert false;
		}
	}

	@Test
	public void test_ExecuteTransaction_ValidTransaction_NewBalancesModifiedByTransactionAmount() throws Exception {
		List<Account> accountDb = new ArrayList<Account>();
		long fromAccountID = 123;
		long toAccountID = 890;
		BigDecimal balanceFrom = BigDecimal.valueOf(25.45);
		BigDecimal balanceTo = BigDecimal.valueOf(100.00);
		BigDecimal amount = BigDecimal.valueOf(12.34);

		Account fromAccount = new Account(fromAccountID, balanceFrom);
		Account toAccount = new Account(toAccountID, balanceTo);
		Transaction transaction = new Transaction(fromAccount.getAccountID(), toAccount.getAccountID(), amount);

		accountDb.add(fromAccount);
		accountDb.add(toAccount);
		accountsDao = new AccountsDao(accountDb);

		accountsDao.executeTransaction(transaction);

		// 25.45 - 12.34 = 13.11
		BigDecimal balanceFromUpdated = BigDecimal.valueOf(13.11);
		// 100.00 + 12.34 = 112.34
		BigDecimal balanceToUpdated = BigDecimal.valueOf(112.34);

		assertEquals(fromAccount.getBalance(), balanceFromUpdated);
		assertEquals(toAccount.getBalance(), balanceToUpdated);
	}

}