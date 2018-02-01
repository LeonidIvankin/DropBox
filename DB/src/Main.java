import java.sql.*;

public class Main {

	private static Connection connection;
	private static Statement statement;
	private static PreparedStatement preparedStatement;


	/*CREATE TABLE users (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name STRING,
    pass STRING
);*/



	public static void main(String[] args) {
		try {
			connect();
			//addData();
			//updateData();
			//deleteData();
			System.out.println(getData("leo", "1111"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}

	}

	private static void addData() throws SQLException {
		statement.executeUpdate("INSERT INTO users (name, pass) VALUES ('leo', '1234')");
	}

	private static void updateData() throws SQLException {
		statement.executeUpdate("UPDATE users SET pass = 1111 WHERE name = 'alex'");
	}

	private static void deleteData() throws SQLException {
		statement.executeUpdate("DELETE FROM users WHERE name = 'alex';");
	}

	private static String getData(String name, String pass){
		try {
			ResultSet resultSet = statement.executeQuery("SELECT id, name, pass, nick FROM users WHERE name = '" + name + "' AND pass = '" + pass + "';");
			return resultSet.getString(4);
		} catch (SQLException e) {
			return null;
		}
	}

	public static void connect() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:DB/dropbox.db");
		statement = connection.createStatement();
	}
	public static void disconnect(){
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
