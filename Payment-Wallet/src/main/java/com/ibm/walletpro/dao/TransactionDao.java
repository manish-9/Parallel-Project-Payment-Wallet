package com.ibm.walletpro.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.walletpro.exceptions.AccountNotFoundException;
import com.ibm.walletpro.exceptions.InsufficientFundException;
import com.ibm.walletpro.exceptions.MinimumBalanceException;
import com.ibm.walletpro.exceptions.PassBookException;
import com.ibm.walletpro.exceptions.WrongPinException;
import com.ibm.walletpro.util.Account;

public interface TransactionDao {
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	
	public int createAccount(String name, double amount)throws MinimumBalanceException;
	
	public void authenticate(int id, int pin)throws AccountNotFoundException, WrongPinException;
	
	public int deposit(int id, double amount);
	
	public int withDraw(int id, double amount)throws InsufficientFundException;
	
	public int transfer(int id, double amount, int id2)throws InsufficientFundException, AccountNotFoundException;
	
	public Account getAccountById(int id);
	
	public int passBook(int id, String operation, double balance) throws PassBookException;
	
}
