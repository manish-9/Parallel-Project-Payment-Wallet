package com.ibm.walletpro.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ibm.walletpro.exceptions.AccountNotFoundException;
import com.ibm.walletpro.exceptions.InsufficientFundException;
import com.ibm.walletpro.exceptions.MinimumBalanceException;
import com.ibm.walletpro.exceptions.PassBookException;
import com.ibm.walletpro.exceptions.WrongPinException;
import com.ibm.walletpro.util.Account;
import com.ibm.walletpro.util.Temps;
import com.ibm.walletpro.util.PassBook;
import com.ibm.walletpro.util.Utils;

@Component("transaction")					// this annotation converts the class into a bean and with id = "transaction"...
public class TransactionDaoImpl implements TransactionDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int createAccount(String name, double amount) throws MinimumBalanceException {
		if (amount <= 0) {
			throw new MinimumBalanceException("Please deposit Initial amount > 0.00");
		}
		int pin = (int) Utils.generatePin();

		int id = (int) Utils.generateId();

		String sqlQuery = "INSERT INTO users VALUES(?,?,?,?)";

		if (jdbcTemplate.update(sqlQuery, id, name, pin, amount) == 1) {
			try {
				passBook(id, "createaccount", amount);
			} catch (PassBookException e) {

				e.printStackTrace();
			}
			Temps.id = id;
			Temps.pin = pin;
			return 1;

		} else {
			System.exit(0);
		}

		return 0;
	}

	// Login authentication of id and pin
	public void authenticate(int id, int pin) throws AccountNotFoundException, WrongPinException {
		Account account = getAccountById(id);
		if (account != null) {

			if (account.getPin() != pin) {
				throw new WrongPinException("Incorrect Pin!");
			} else {
				Temps.id = id;
				Temps.pin = pin;
			}
		} else {
			throw new AccountNotFoundException("Requested Account was NOT Found!");
		}
	}

	public int deposit(int id, double amount) {

		Account account = getAccountById(id);
		System.out.println("ID : " + id);
		double total = account.getAmount() + amount;
		String sql = "UPDATE users SET account_balance = ? WHERE user_id = ?";

		if (jdbcTemplate.update(sql, total, id) == 1) {
			try {
				passBook(id, "deposited " + Double.toString(amount), total);
			} catch (PassBookException e) {

				e.printStackTrace();
			}
			return 1;
		} else {
			return 0;
		}
	}

	public int withDraw(int id, double amount) throws InsufficientFundException {
		Account account = getAccountById(id);
		double total = account.getAmount() - amount;
		if (total < 100) {
			throw new InsufficientFundException("Sorry! You cannot make this withrawal due to low Funds!");
		}

		String sql = "UPDATE users SET account_balance = ? WHERE user_id = ?";

		if (jdbcTemplate.update(sql, total, id) == 1) {
			try {
				passBook(id, " withdrawal of " + Double.toString(amount), total);
			} catch (PassBookException e) {
				e.printStackTrace();
			}
			return (int) total;
		} else {
			return -1;
		}

	}

	public int transfer(int idFrom, double amount, int idTo)
			throws InsufficientFundException, AccountNotFoundException {
		Account account = getAccountById(idFrom);
		double total = account.getAmount() - amount;
		if (total < 1) {
			throw new InsufficientFundException("You don't have Sufficient Funds for this Request!");
		}

		String sql = "UPDATE users SET account_balance = ? WHERE user_id = ?";
		Account account1 = getAccountById(idTo);

		if (account1 == null) {
			throw new AccountNotFoundException("There is NO such Account to make this Transfer!");
		}
		if (jdbcTemplate.update(sql, total, idFrom) == 1) {

			deposit(idTo, amount);
			try {
				passBook(idFrom, "Transfered " + Double.toString(amount), total);
			} catch (PassBookException e) {
				e.printStackTrace();
			}
			return (int) total;
		} else {
			return -1;
		}

	}

	public Account getAccountById(int id) {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { id },
					new org.springframework.jdbc.core.RowMapper<Account>() {
				public Account mapRow(java.sql.ResultSet resultSet, int rowNumber)
						throws java.sql.SQLException {
					Account account = new Account();
					account.setId(resultSet.getInt("user_id"));
					account.setName(resultSet.getString("user_name"));
					account.setAmount(resultSet.getDouble("account_balance"));
					account.setPin(resultSet.getInt("account_pin"));
					return account;
				}
			});
		} catch (Exception e) {
			return null;
		}
	}

	// To view the previous transactions
	public int passBook(int id, String operation, double balance) throws PassBookException {
		String idd = "pb" + Integer.toString((int) id);
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		if (operation.equals("getBalance")) {

			// returns the checkbook
			System.out.println("TimeStamp	|	Operation	|	Balance    ");
			Temps.pbk = (ArrayList<PassBook>) getAllInfo(idd);

			return 1;
		} else if (operation.equals("createaccount")) {
			String sql = "create table " + idd + " (timestamp varchar(25), operation varchar(50), balance numeric(10))";

			if (jdbcTemplate.update(sql) != 1) {

			} else {

				throw new PassBookException("something terrible happened during account creation");
			}

			String sql1 = "INSERT INTO " + idd + " values(? , ?, ?)";

			if (jdbcTemplate.update(sql1, ts.toString(), operation, balance) == 1) {

			} else {
				throw new PassBookException("something terrible happened during account updation");

			}
		} else {
			String sql1 = "INSERT INTO " + idd + " values(?, ?, ?)";

			if (jdbcTemplate.update(sql1, ts.toString(), operation, balance) == 1) {

			} else {
				throw new PassBookException("failed to update passbook");
			}
		}
		return 0;
	}

	public List<PassBook> getAllInfo(String id) {
		String sql = "SELECT * FROM " + id;
		BigDecimal bd;

		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		List<PassBook> pbList = new ArrayList<PassBook>();

		for (Map<String, Object> map : list) {
			PassBook passBook = new PassBook();
			passBook.setOperation((String) map.get("operation"));
			passBook.setTimestamp((String) map.get("timestamp"));
			bd = (BigDecimal) map.get("balance");
			passBook.setBalance(bd.doubleValue());
			pbList.add(passBook);
		}
		return pbList;
	}
}
