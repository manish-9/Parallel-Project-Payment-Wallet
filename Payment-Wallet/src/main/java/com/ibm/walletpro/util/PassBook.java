package com.ibm.walletpro.util;

public class PassBook {

	String timestamp;
	String operation;
	double balance;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "PassBook [timestamp=" + timestamp + ", operation=" + operation + ", balance=" + balance + "]";
	}

}
