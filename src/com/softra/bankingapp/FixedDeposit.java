package com.softra.bankingapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.softra.bankingapp.exception.InvalidInputException;

public class FixedDeposit extends Account {
	private int tenure;
	private double interestRate;
	private static int count=0;
	public FixedDeposit(String branchName, double depositAmount, int tenure, double interestRate) throws InvalidInputException
	{
		super(2000000000+(++count), branchName);
		if(tenure > 5 || tenure < 1)
		{
			//throw exception
			throw new InvalidInputException("Tenure must be in the range of 1 to 5");
		}
		else if(interestRate < 0.04 || interestRate >0.065){
			//throw exception
			throw new InvalidInputException("Interest rate must be in the range of 4% to 6.5%");
		}
		else if(depositAmount < 0)
		{
			//throw exception
			throw new InvalidInputException("Balance cannot be negative");
		}
		if(depositAmount <50)
		{
			//throw exception
			throw new InvalidInputException("Insufficient balance for account creation");
		}
		super.setBalance(depositAmount);
		this.tenure = tenure;
		double ir1 = 0;
		double ir2 = 0;
		switch(tenure)
		{
		case 1:
		case 2:
		{
			ir1 = 0.04;
			ir2 = 0.045;
			break;
		}
		case 3:
		case 4:
		{
			ir1 = 0.05;
			ir2 = 0.055;
			break;
		}
		case 5:
		{
			ir1 = 0.06;
			ir2 = 0.065;
			break;
		}
		}
		if(interestRate != ir1 && interestRate != ir2)
			throw new InvalidInputException("Incorrect interest rate");
		this.interestRate = interestRate;
		Connection con = null;
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(Utility.getFile()));
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String username = p.getProperty("username");
			String password = p.getProperty("password");
			//System.out.println(driver +" "+ url +" "+url +" "+ username+" "+password);
			Class.forName(driver);
			con =DriverManager.getConnection(url, username, password);
			//System.out.println("FDA con: "+con);
			String insert ="INSERT INTO fixed_deposit_account(accountNumber, balance, openingDate, branchName, tenure, interestRate) values(?,?,?,?,?,?)";
			PreparedStatement pt = con.prepareStatement(insert);
			pt.setInt(1, 2000000000+(count));
			pt.setDouble(2, depositAmount);
			pt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			pt.setString(4, branchName);
			pt.setInt(5,tenure);
			pt.setDouble(6, interestRate);
			pt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void setAccountNumber(int accountNumber) throws InvalidInputException {
		// TODO Auto-generated method stub
		if(accountNumber > 2000000000)
		{
			synchronized(this) {
				super.setAccountNumber(accountNumber);
				
			}
		}
		else
		{
			throw new InvalidInputException("account number must start with 2");
		}
	}
	@Override
	public double calculateInterest() {
		synchronized(this)
		{
			double curBalance = this.getBalance();
			int period = Utility.dateDiff2(this.getOpeningDate());
			System.out.println("Account with balance: "+curBalance+" over period: "+ period+" years");
			return curBalance * period * interestRate;
		
		}
	}
	

}
