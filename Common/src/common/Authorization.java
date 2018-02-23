package common;

import server.ClientHandler;
import server.Server;

public class Authorization {
	private Server server = null;
	private ClientHandler clientHandler;
	private boolean isAuthorized = false;
	private WorkWithPacket workWithPacket;

	public Authorization(ClientHandler clientHandler, WorkWithPacket workWithPacket){
		this.clientHandler = clientHandler;
		this.server = clientHandler.getServer();
		this.workWithPacket = workWithPacket;
	}

	public void signIn(Object body) {//авторизация

		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loggedIntoAccount = server.checkLoginAndPass(name, pass);
		if (loggedIntoAccount) { // если пользователь указал правильные логин/пароль
			if (!server.isAccountBusy(name)) {
				workWithPacket.sendPacket(Constant.AUTHOK, null);
				clientHandler.loginToAccount(name);
				isAuthorized = true;

			} else sendMessage("Учетная запись уже используется");
		} else sendMessage("Не верные логин/пароль");
	}

	public void signUp(Object body) {//регистрация
		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loginIsReserved = server.checkLogin(name);
		if (name.equals("") && pass.equals("")) {
			sendMessage("Введите логин и пароль и повторите");
		} else if (name.equals("")) {
			sendMessage("Введите логин");
		} else {
			if (loginIsReserved) {
				sendMessage("Логин занят");
			} else if (pass.equals("")) {
				sendMessage("Логин свободен. Введите пароль и повторите");
			} else {
				System.out.println(server.setLoginAndPass(name, pass));
				clientHandler.makeDir(name);
				sendMessage("Вы успешно зарегистрированы");
				signIn(body);
			}
		}
	}

	public void sendMessage(String msg){
		clientHandler.sendMessage(msg);
	}
}
