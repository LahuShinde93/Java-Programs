package com.address.book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Contact {
	private String name;
	private String phoneNumber;
	private String email;

	public Contact(String name, String phoneNumber, String email) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email;
	}
}

public class PersonalAddressBook {
	public static void main(String[] args) {
		Connection connection = null;
		try {
			// Connect to the Oracle database
			String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:ORCL";
			String username = "system";
			String password = "tiger";
			connection = DriverManager.getConnection(jdbcUrl, username, password);
			System.out.println("Connected to the database.");

			AddressBook addressBook = new AddressBook(connection);
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.println("Personal Address Book");
				System.out.println("1. Add Contact");
				System.out.println("2. Display Contacts");
				System.out.println("3. Exit");
				System.out.print("Select an option: ");

				int choice = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				switch (choice) {
				case 1:
					System.out.print("Enter Name: ");
					String name = scanner.nextLine();
					System.out.print("Enter Phone Number: ");
					String phoneNumber = scanner.nextLine();
					System.out.print("Enter Email: ");
					String email = scanner.nextLine();
					Contact contact = new Contact(name, phoneNumber, email);
					addressBook.addContact(contact);
					System.out.println("Contact added successfully!");
					break;
				case 2:
					System.out.println("Contacts in Address Book:");
					List<Contact> contacts = addressBook.getContacts();
					for (Contact c : contacts) {
						System.out.println(c);
					}
					break;
				case 3:
					System.out.println("Exiting...");
					scanner.close();
					System.exit(0);
				default:
					System.out.println("Invalid option. Please try again.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
					System.out.println("Disconnected from the database.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

class AddressBook {
	private Connection connection;

	public AddressBook(Connection connection) {
		this.connection = connection;
	}

	public void addContact(Contact contact) throws SQLException {
		String sql = "INSERT INTO Contacts (Name, PhoneNumber, Email) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, contact.getName());
			statement.setString(2, contact.getPhoneNumber());
			statement.setString(3, contact.getEmail());
			statement.executeUpdate();
		}
	}

	public List<Contact> getContacts() throws SQLException {
		List<Contact> contacts = new ArrayList<>();
		String sql = "SELECT Name, PhoneNumber, Email FROM Contacts";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				String name = resultSet.getString("Name");
				String phoneNumber = resultSet.getString("PhoneNumber");
				String email = resultSet.getString("Email");
				Contact contact = new Contact(name, phoneNumber, email);
				contacts.add(contact);
			}
		}
		return contacts;
	}
}
