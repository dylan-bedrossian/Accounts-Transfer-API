package dylanb.revolut.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dylanb.revolut.core.Account;
import dylanb.revolut.core.Transaction;
import dylanb.revolut.db.AccountsDao;
import dylanb.revolut.exception.ExistingAccountException;
import dylanb.revolut.exception.InvalidTransactionException;
import dylanb.revolut.exception.NegativeBalanceException;
import dylanb.revolut.exception.NegativeTransactionAmountException;

import java.util.NoSuchElementException;
import static java.lang.Long.parseLong;
import static spark.Spark.*;

public class AccountMapper {

	private AccountsDao accountsDao;
	private ObjectMapper objectMapper;

	public AccountMapper(AccountsDao accountsDao, ObjectMapper objectMapper) {
		this.accountsDao = accountsDao;
		this.objectMapper = objectMapper;
	}

	private void sendAccountGetRequest() {
		get("/account/id", (request, response) -> {
			String id = request.params("id");
			response.type("application/json");
			try {
				return objectMapper.writeValueAsString(accountsDao.getAccount(parseLong(id)));
			} catch (NoSuchElementException e) {
				halt(404, "The account " + id + " does not exist");
				throw e;
			}
		});
	}

	private void sendTransactionPostRequest() {
		post("/account/transaction", "multipart/form-data", (request, response) -> {
			String body = request.body();
			try {
				Transaction transaction = objectMapper.readValue(body, Transaction.class);
				accountsDao.executeTransaction(transaction);
				response.status(200);
			} catch (InvalidTransactionException | NegativeBalanceException | NegativeTransactionAmountException e) {
				halt(403, e.getMessage());
			}
			return "";
		});
	}

	private void sendAccountPostRequest() {
		post("/account", "multipart/form-data", (request, response) -> {
			String body = request.body();
			Account account = objectMapper.readValue(body, Account.class);
			try {
				accountsDao.addAccount(account);
				response.status(201);
			} catch (ExistingAccountException e) {
				halt(409, e.getMessage());
			}
			return "";
		});
	}

	public void mapRoutes() {
		sendAccountGetRequest();
		sendTransactionPostRequest();
		sendAccountPostRequest();
	}

}
