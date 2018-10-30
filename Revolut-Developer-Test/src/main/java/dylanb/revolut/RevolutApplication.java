package dylanb.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import dylanb.revolut.db.AccountsDao;
import dylanb.revolut.mappers.AccountMapper;

public class RevolutApplication {
	private final AccountsDao accountsDao;
	private final AccountMapper accountMapper;

	public RevolutApplication(final AccountsDao accountsDao, final AccountMapper accountMapper) {
		this.accountsDao = accountsDao;
		this.accountMapper = accountMapper;
		this.accountMapper.mapRoutes();
	}

	public static void main(String[] args) {
		final AccountsDao accountsDao = new AccountsDao();
		final RevolutApplication revolutApplication = new RevolutApplication(accountsDao,
				new AccountMapper(accountsDao, new ObjectMapper()));
	}
}