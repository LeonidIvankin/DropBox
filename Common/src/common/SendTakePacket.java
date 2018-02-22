package common;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendTakePacket {
	private ObjectOutputStream out;

	public SendTakePacket(ObjectOutputStream out){
		this.out = out;

	}

	public void sendPacket(String head, Object body) {//отправить объект
		Object[] packet = {head, body};
		try {
			out.writeObject(packet);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
