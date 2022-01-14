package com.softra.bankingapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeSet;

import com.softra.bankingapp.exception.CustomerNotFoundException;
import com.softra.bankingapp.exception.InvalidInputException;

class AccountComparator implements Comparator<Account>{

	@Override
	public int compare(Account arg0, Account arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
public class Customer {
	private static int count = 0;
	private int customerId; 
	private String firstName,lastName;
	private int age;
	private int mobileNumber;
	private TreeSet<Account> accounts;
	public Customer(String firstName, String lastName, int age, int mobileNumber) throws InvalidInputException
	{
		int tempId = ++count;
		this.customerId = tempId;
		if(Utility.isValidName(firstName, lastName))
		{
			this.firstName = firstName;
			this.lastName = lastName;
		}
		else
		{
			//throw exception
			throw new InvalidInputException("Name can only contain alphabets");
		}
		this.age = age;
		this.mobileNumber = mobileNumber;
		accounts = new TreeSet<Account>((o1, o2)-> o1.getAccountNumber()-o2.getAccountNumber());
		//add to db
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
			//System.out.println("con: "+con);
			String insert ="INSERT INTO customer(customerId, firstName, lastName, age, mobileNumber) values(?,?,?,?,?)";
			PreparedStatement pt = con.prepareStatement(insert);
			pt.setInt(1, tempId);
			pt.setString(2, firstName);
			pt.setString(3, lastName);
			pt.setInt(4, age);
			pt.setInt(5, mobileNumber);
			pt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
		//System.out.println("valid name: "+Utility.isValidName(firstName, lastName));
	}
//	public Customer(String firstName, String lastName, int age, int mobileNumber, Account account)
//	{
//		this.customerId = ++count;
//		if(Utility.isValidName(firstName, lastName))
//		{
//			this.firstName = firstName;
//			this.lastName = lastName;
//		}
//		else
//		{
//			//throw exception
//		}
//		this.age = age;
//		this.mobileNumber = mobileNumber;
//		try {
//			this.addAccount(account);
//		} catch (InvalidInputException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getFullName()
	{
		return firstName +" " +lastName;
	}
	public void setFullName(String firstName, String lastName) throws InvalidInputException
	{
		if(Utility.isValidName(firstName, lastName))
		{
			this.firstName = firstName;
			this.lastName = lastName;
		}
		else
			throw new InvalidInputException("Name can only contain alphabets");
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(int mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public TreeSet<Account> getAccount() {
		return accounts;
	}
	public void addAccount(Account a) throws InvalidInputException {
		if(accounts.size()>0 && accounts.first() instanceof SavingsAccount &&a instanceof SavingsAccount )
		{
			//throw exception
			throw new InvalidInputException("Each customer can only have one savings account");
		}
		else {
			synchronized(this) {
				accounts.add(a);
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
					String fk = "UPDATE "+(a instanceof SavingsAccount?"savings_account":"fixed_deposit_account")+" SET customerId = "+ this.customerId+ " WHERE accountNumber = "+a.getAccountNumber();
					PreparedStatement pt = con.prepareStatement(fk);
					pt.execute();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					try {
						con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void searchCustomer(int customerId)
	{
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con =DriverManager.getConnection("jdbc:mysql://localhost:3307/banking_system", "root", "");

			String select ="SELECT * FROM customer where customerId = ?";
			//System.out.println("s con: "+con);
			PreparedStatement stmt = con.prepareStatement(select);
			stmt.setInt(1, customerId);
			System.out.println(stmt);
			ResultSet result = stmt.executeQuery();
			if(result.first())
			{
				int id = result.getInt(1);
				String firstName = result.getString(2);
				String lastName = result.getString(3);
				int age = result.getInt(4);
				System.out.println("Found Customer id: "+ id+" firstName: "+firstName+" lastName: " +lastName+" age: " +age);
			}
			else
			{
				throw new CustomerNotFoundException("Customer with "+customerId+" does not exist");
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CustomerNotFoundException e) {
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
		return "Customer [customerId=" + customerId + ", firstName=" + firstName + ", lastName=" + lastName + ", age="
				+ age + ", mobileNumber=" + mobileNumber + ", accounts=" + accounts + "]";
	}
	
}
