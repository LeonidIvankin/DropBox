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
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}

	}

	private static void addData() throws SQLException {
		statement.executeUpdate("INSERT INTO users (name, pass) VALUES ('n1', '1234')");
	}

	private static void updateData() throws SQLException {
		statement.executeUpdate("UPDATE users SET pass = 1111 WHERE name = 'n2'");
	}

	private static void deleteData() throws SQLException {
		statement.executeUpdate("DELETE FROM users WHERE name = 'n2';");
	}

	private static void getData() throws SQLException {
		ResultSet resultSet = statement.executeQuery("SELECT id, name FROM users WHERE name = 'n1';");
		while(resultSet.next()) System.out.println(resultSet.getInt("id") + " " + resultSet.getString("name"));
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
