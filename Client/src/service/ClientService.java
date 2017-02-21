package service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.ChatMessage;

public class ClientService {


	private Socket socket;
	private ObjectOutputStream output;

	public Socket connect() {
		try {
			this.socket = new Socket("localhost", 5555);
			this.output = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException ex) {
			Logger.getLogger(ClientService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ClientService.class.getName()).log(Level.SEVERE, null, ex);
		}

		return socket;
	}

	public void send(ChatMessage message) {
		try {
			output.writeObject(message);
		} catch (IOException ex) {
			Logger.getLogger(ClientService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}




}
