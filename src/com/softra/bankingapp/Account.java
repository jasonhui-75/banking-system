package com.softra.bankingapp;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.softra.bankingapp.exception.AccountNotFoundException;
import com.softra.bankingapp.exception.CustomerNotFoundException;
import com.softra.bankingapp.exception.InvalidInputException;

public abstract class Account implements Comparable<Account>{
	private int accountNumber;
	private double balance;
	private static int count = 0;
	private static String bankName = "HSBC";
	private String branchName;
	private String openingDate;
	
	//private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 
	public Account(int accountNumber, String branchName)
	{
		this.accountNumber = accountNumber;
		this.branchName = branchName;
		this.openingDate = LocalDate.now().format(dtf);
		this.balance = 0;
	}
//	public Account(double balance, String branchName)
//	{
//		
//		this.branchName= branchName;
//		this.accountNumber = ++count;
//		this.openingDate = df.format(new Date());
//		
//	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(int accountNumber) throws InvalidInputException {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(String date) throws InvalidInputException {
		//String date = String.format("%d/%d/%d", day, month, year);
//		System.out.println("date: " +date);
//		System.out.println(Utility.isValidDate(date));
		int year, month, day;
		String[] input = date.split("/");
		if(input.length != 3)
		{
			//throw exception
			throw new InvalidInputException("Invalid format");
		}
		day= Integer.valueOf(input[0]);
		month = Integer.valueOf(input[1]);
		year = Integer.valueOf(input[2]);
		//System.out.println("got this date: "+day+"/"+month+"/"+year);
		if(Utility.isValidDate2(date))
		{
			this.openingDate = LocalDate.parse(date, dtf).format(dtf);
		}
		
	}
	protected abstract double calculateInterest();
	
	public static void searchAccount(int accountNumber)
	{
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con =DriverManager.getConnection("jdbc:mysql://localhost:3307/banking_system", "root", "");
			boolean isFixedDeposit = accountNumber>2000000000;
			String select ="SELECT * FROM "+(isFixedDeposit?"fixed_deposit_account":"savings_account")+" WHERE accountNumber = ?";
			//System.out.println("s con: "+con);
			PreparedStatement stmt = con.prepareStatement(select);
			stmt.setInt(1, accountNumber);
			System.out.println(stmt);
			ResultSet result = stmt.executeQuery();
			if(result.first())
			{
				if(isFixedDeposit)
				{
					int id = result.getInt(1);
					String branchName = result.getString(2);
					String bankName = result.getString(3);
					String openingDate = result.getDate(4).toString();
					int tenure = result.getInt(5);
					double interestRate = result.getDouble(6);
					double balance = result.getDouble(7);
					System.out.println("Found Fixed Deposit Account id: "+ id+" branchName: "+branchName+" bankName: " +bankName+" openingDate: " +openingDate +" tenure: " +tenure +" interestRate: " +interestRate+" balance: " +balance);
				}
				else {
					int id = result.getInt(1);
					String branchName = result.getString(2);
					double balance = result.getDouble(3);
					String bankName = result.getString(4);
					double aud = result.getDouble(5);
					double sgd = result.getDouble(6);
					boolean isSalaryAccount = result.getBoolean(7);
					String openingDate = result.getDate(8).toString();
					System.out.println("Found Savings Account id: "+ id+" balance: " +balance+" branchName: "+branchName+" bankName: " +bankName+" openingDate: " +openingDate +" aud: " +aud +" sgd: " +sgd+" isSalaryAccount: " +isSalaryAccount);
				}
			}
			else
			{
				throw new AccountNotFoundException("Customer with "+accountNumber+" does not exist");
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			System.out.println(e.getMessage());
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", balance=" + balance + ", branchName=" + branchName
				+ ", openingDate=" + openingDate + "]";
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Account)
		{
			Account a = (Account)arg0;
			return (this.accountNumber == a.accountNumber)?true:false;
		}
		return super.equals(arg0);
	}

	@Override
	public int compareTo(Account o) {
		// TODO Auto-generated method stub
		if(this.balance < o.balance)
			return -1;
		else if(this.balance == o.balance)
			return 0;
		return 1;
	}
	

}
