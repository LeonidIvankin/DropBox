package db;

import java.sql.*;

public class DBService {
	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;

	/*CREATE TABLE users (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name STRING,
    pass STRING
);*/

/*	public db.DBService() {
		try {
			connect();
			//addData();
			//updateData();
			//deleteData();
			checkLoginAndPass();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}*/

	private void addData() throws SQLException {
		statement.executeUpdate("INSERT INTO users (name, pass) VALUES ('leo', '1234')");
	}

	public boolean addData(String name, String pass){

		try {
			statement.executeUpdate("INSERT INTO users (name, pass) VALUES ('" + name + "', '" + pass + "')");
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}



	private void deleteData() throws SQLException {
		statement.executeUpdate("DELETE FROM users WHERE name = 'alex';");
	}

	private void checkLoginAndPass() throws SQLException {
		ResultSet resultSet = statement.executeQuery("SELECT id, name FROM users WHERE name = 'leo';");
		while(resultSet.next()) {
			System.out.println(resultSet.getInt("id") + " " + resultSet.getString("name"));
		}
	}

	public boolean checkLoginAndPass(String name, String pass){
		try {
			ResultSet resultSet = statement.executeQuery("SELECT pass FROM users WHERE name = '" + name + "' AND pass = '" + pass + "';");
			return resultSet.getString("pass").equals(pass);
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public boolean checkLogin(String name){
		try {
			ResultSet resultSet = statement.executeQuery("SELECT pass FROM users WHERE name = '" + name + "';");
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
