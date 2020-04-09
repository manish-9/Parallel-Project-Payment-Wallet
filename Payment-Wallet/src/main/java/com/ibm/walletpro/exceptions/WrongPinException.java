package com.ibm.walletpro.exceptions;

@SuppressWarnings("serial")
public class WrongPinException extends Exception {
	String st = "You have entered wrong pin";

	public WrongPinException(String st) {
		super(st);
		// TODO Auto-generated constructor stub
	}
	
	
}
