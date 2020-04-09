package com.ibm.walletpro.exceptions;

public class AccountNotFoundException extends Exception{
	
	String st = "The account that you are searching is not available";

	public AccountNotFoundException(String st) {
		super(st);
		// TODO Auto-generated constructor stub
	}
	

}
