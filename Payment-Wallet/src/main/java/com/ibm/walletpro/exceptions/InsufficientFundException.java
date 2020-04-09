package com.ibm.walletpro.exceptions;

@SuppressWarnings("serial")
public class InsufficientFundException extends Exception{

	String st = "looks like you dont have sufficient funds";

	public InsufficientFundException(String st) {
		super(st);
		// TODO Auto-generated constructor stub
	}
	
	
}
