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
		String login = (String) objects[0];
		String pass = (String) objects[1];

		boolean loggedIntoAccount = server.checkLoginAndPass(login, pass);
		if (loggedIntoAccount) { // если пользователь указал правильные логин/пароль
			if (!server.isAccountBusy(login)) {
				workWithPacket.sendPacket(Constant.AUTHOK, null);
				clientHandler.loginToAccount(login);
				isAuthorized = true;

			} else sendMessage(Constant.ACCOUNT_IS_USED);
		} else sendMessage(Constant.INVALID_LOGIN_AND_PASSWORD);
	}

	public void signUp(Object body) {//регистрация
		Object[] objects = (Object[]) body;
		String login = (String) objects[0];
		String pass = (String) objects[1];

		boolean loginIsReserved = server.checkLogin(login);
		if (login.equals("") && pass.equals("")) {
			sendMessage(Constant.ENTER_YOUR_LOGIN_AND_PASSWORD_AND_REPEAT);
		} else if (login.equals("")) {
			sendMessage(Constant.ENTER_LOGIN);
		} else {
			if (loginIsReserved) {
				sendMessage(Constant.LOGIN_BUSY);
			} else if (pass.equals("")) {
				sendMessage(Constant.LOGIN_IS_FREE);
			} else {
				System.out.println(server.setLoginAndPass(login, pass));
				clientHandler.makeDir(login);
				sendMessage(Constant.YOU_HAVE_SUCCESSFULLY_REGISTERED);
				signIn(body);
			}
		}
	}

	public void sendMessage(String msg){
		clientHandler.sendMessage(msg);
	}
}
