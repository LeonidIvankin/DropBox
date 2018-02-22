package common;

import server.ClientHandler;
import server.Server;

public class Authorization {
	private Server server = null;
	private ClientHandler clientHandler;
	private boolean isAuthorized = false;
	private SendTakePacket sendTakePacket;

	public Authorization(ClientHandler clientHandler, SendTakePacket sendTakePacket){
		this.clientHandler = clientHandler;
		this.server = clientHandler.getServer();
		this.sendTakePacket = sendTakePacket;
	}

	public void signIn(Object body) {//авторизация

		Object[] objects = (Object[]) body;
		String name = (String) objects[0];
		String pass = (String) objects[1];

		boolean loggedIntoAccount = server.checkLoginAndPass(name, pass);
		if (loggedIntoAccount) { // если пользователь указал правильные логин/пароль
			if (!server.isAccountBusy(name)) {
				sendTakePacket.sendPacket(Constant.AUTHOK, null);
				clientHandler.setName(name);
				String[] files = clientHandler.getFiles(name);
				if (files.length != 0) {
					clientHandler.reload();
					sendMessage(name + ", ваши файлы");
				} else sendMessage(name + ", ваша папка пока пуста");
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
