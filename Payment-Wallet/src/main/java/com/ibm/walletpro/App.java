package com.ibm.walletpro;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.walletpro.dao.TransactionDao;
import com.ibm.walletpro.dao.TransactionDaoImpl;
import com.ibm.walletpro.exceptions.AccountNotFoundException;
import com.ibm.walletpro.exceptions.InsufficientFundException;
import com.ibm.walletpro.exceptions.MinimumBalanceException;
import com.ibm.walletpro.exceptions.PassBookException;
import com.ibm.walletpro.exceptions.WrongPinException;
import com.ibm.walletpro.util.Account;
import com.ibm.walletpro.util.Temps;
import com.ibm.walletpro.util.PassBook;

public class App {
	static TransactionDao transactionImpl;
	static Scanner sc;

	public static void main(String[] args) {
		System.out.println("Welcome");
		ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
		transactionImpl = (TransactionDao) context.getBean("transaction");

		sc = new Scanner(System.in);

		System.out.println("Enter \n 1. To create a new Account \n 2. To Use your existing Account \n 3. Exit");
		int choice = 0;
		try {
			choice = sc.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("Please enter the correct input type \n Try again \n Thank u!!!");
		}

		if (choice == 1) {
			createAcc();
		} else if (choice == 2) {
			authenticate();
		} else if (choice == 3) {
			System.out.println("bye !!");
			System.exit(0);
		} else {
			System.out.println("looks like you have entered wrong choice. \n bye!!!");
			System.exit(0);
		}
		while (true) {
			System.out.println(
					"Which Operation do you want to use: \n 1) Deposit \n 2) Withdraw \n 3) Get Balance \n 4) Transfer\n 5) View Past Transactions\n6.Exit");
			int select = 0;
			try {
				select = sc.nextInt();
			} catch (InputMismatchException e) {
				System.err.println("Integer Value Expected!");
			}
			switch (select) {
			case 1:
				deposit();
				break;
			case 2:
				withdraw();
				break;
			case 3:
				getBalance();
				break;
			case 4:
				transfer();
				break;
			case 5:
				passBk();
				break;
			case 6:
				System.exit(0);
				break;
			default:
				System.err.println(
						"We are still working on that! \n In the meantime, please select one of the available services");
				break;
			}
		}

	}

	private static void passBk() {

		try {
			if (transactionImpl.passBook(Temps.id, "getBalance", 0) == 1) {
				System.out.println("Your TransactionDao Recaord\n| TimeStamp      |   Operation     | balance");

				for (PassBook pbkk : Temps.pbk) {
					System.out.println(pbkk.getTimestamp() + " | " + pbkk.getOperation() + " | " + pbkk.getBalance());
				}
				System.out.println();
			} else {
				System.err.println("Could Not Find Passbook!");
			}
		} catch (PassBookException e) {
			e.printStackTrace();
		}
	}

	private static int transfer() {
		System.out.println("Transfer Amount: ");
		double amount = 0;
		try {
			amount = sc.nextDouble();
		} catch (InputMismatchException e) {
			e.printStackTrace();
			return 1;
		}

		System.out.println("Receivers Account ID: ");
		int id2 = 0;
		try {
			id2 = sc.nextInt();
		} catch (InputMismatchException e) {
			e.printStackTrace();
			return 1;
		}
		try {
			int amnt = transactionImpl.transfer(Temps.id, amount, id2);
			if (amnt != -1) {
				System.out.println("Transfer Successfull \nCurrent Balance:" + amnt);
			} else {
				System.out.println("Something went wrong");
			}

		} catch (InsufficientFundException e) {
			System.out.println(e.getMessage());
		} catch (AccountNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private static void getBalance() {

		Account acc = transactionImpl.getAccountById(Temps.id);
		System.out.println("Current Balance: " + acc.getAmount());

	}

	private static int withdraw() {
		System.out.println("Enter the amount that you want to withdraw");
		double amount = 0;
		try {
			amount = sc.nextDouble();
		} catch (InputMismatchException e) {
			e.printStackTrace();
			return 1;
		}
		try {
			int amnt = transactionImpl.withDraw(Temps.id, amount);
			if (amnt != -1) {
				System.out.println("With Draw Successfull \nCurrent Balance:" + amnt);
			} else {
				System.out.println("Something went wrong");
			}
		} catch (InsufficientFundException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	private static int deposit() {
		System.out.println("enter the amount that you want to Deposit:");
		double amount = 0;
		try {
			amount = sc.nextDouble();
		} catch (InputMismatchException e) {
			e.printStackTrace();
			return 1;
		}
		if (transactionImpl.deposit(Temps.id, amount) == 1) {
			System.out.println("Deposit successfull");
		} else {
			System.out.println("Something went wrong during deposit");
		}
		return 0;
	}

	private static void authenticate() {
		try {
			System.out.println("Enter the ID:");
			Temps.id = sc.nextInt();
			System.out.println("Enter the pin:");
			Temps.pin = sc.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("looks like there was an exception please re do the authentication process");
			System.exit(0);
		}
		try {
			transactionImpl.authenticate(Temps.id, Temps.pin);
		} catch (AccountNotFoundException e) {
			System.out.println(e.getMessage());
			;
			System.exit(0);
		} catch (WrongPinException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}

	private static void createAcc() {
		String name;
		double amount;

		System.out.println("Enter your Name: ");

		sc.nextLine();
		name = sc.nextLine();
		System.out.println("Enter the amount that you want to deposit initially");
		amount = sc.nextDouble();
		try {
			if (transactionImpl.createAccount(name, amount) == 1) {
				System.out.println("Account created successfully");
				System.out.println("Credentials :" + "\nAccount Id:" + Temps.id + "\nAccount Pin: " + Temps.pin);
			} else {

			}
		} catch (MinimumBalanceException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.out.println("bye!!!");
			System.exit(0);
		}
	}
}
