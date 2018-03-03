package db;

import java.sql.*;

public class DBService {
	private Connection connection;
	private Statement statement;

	public boolean addData(String login, String pass){

		try {
			statement.executeUpdate("INSERT INTO users (login, pass) VALUES ('" + login + "', '" + pass + "')");
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	private void deleteData() throws SQLException {
		statement.executeUpdate("DELETE FROM users WHERE login = 'alex';");
	}

	private void checkLoginAndPass() throws SQLException {
		ResultSet resultSet = statement.executeQuery("SELECT id, name FROM users WHERE login = 'leo';");
		while(resultSet.next()) {
			System.out.println(resultSet.getInt("id") + " " + resultSet.getString("login"));
		}
	}

	public boolean checkLoginAndPass(String login, String pass){
		try {
			ResultSet resultSet = statement.executeQuery("SELECT pass FROM users WHERE login = '" + login + "' AND pass = '" + pass + "';");
			return resultSet.getString("pass").equals(pass);
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public boolean checkLogin(String login){
		try {
			ResultSet resultSet = statement.executeQuery("SELECT pass FROM users WHERE login = '" + login + "';");
			return resultSet.getString("pass") != null;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public void connect() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:DB/dropbox.db");
		statement = connection.createStatement();
	}

	public void disconnect(){
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
