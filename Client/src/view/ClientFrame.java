package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import bean.ChatMessage;
import bean.ChatMessage.Action;
import service.ClientService;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.DropMode;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ClientFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtName;
	private Socket socket;
	private ChatMessage message;
	private ClientService service;
	private JButton btconect;
	private JButton btnSair;
	private JTextArea txtRecive ;
	
	private JButton btsend;
	private JScrollPane scrollPane_1;
	private JTextArea txtMessage;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientFrame frame = new ClientFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public ClientFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 466, 335);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Conectar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(12, 12, 329, 55);
		contentPane.add(panel);
		panel.setLayout(null);

		txtName = new JTextField();
		txtName.setBounds(12, 24, 114, 19);
		panel.add(txtName);
		txtName.setColumns(10);

		btconect = new JButton("Conectar");
		btconect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = txtName.getText();

				if (!name.isEmpty()) {
					message = new ChatMessage();
					message.setAction(Action.CONNECT);
					message.setName(name);

					service = new ClientService();
					socket = service.connect();

					new Thread(new ListenerSocket(socket)).start();

					service.send(message);
				}
			}
		});
		btconect.setBounds(138, 24, 100, 19);
		panel.add(btconect);

		btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.setAction(Action.DISCONNECT);
				disconnect();
				txtName.setEnabled(true);
				btnSair.setEnabled(false);
				txtMessage.setEnabled(false);
			}
		});

		btnSair.setEnabled(false);
		btnSair.setBounds(251, 24, 66, 19);
		panel.add(btnSair);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Online", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(353, 12, 97, 252);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JList listOnlineUsers = new JList();
		listOnlineUsers.setBounds(12, 23, 73, 217);
		panel_1.add(listOnlineUsers);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(68, 23, 17, 153);
		panel_1.add(scrollBar);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.setBounds(22, 79, 319, 185);
		contentPane.add(panel_2);
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
				.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
		);
		
		txtMessage = new JTextArea();
		scrollPane_1.setViewportView(txtMessage);
		
		txtRecive = new JTextArea();
		txtRecive.setWrapStyleWord(true);
		txtRecive.setLineWrap(true);
		txtRecive.setEditable(false);
		scrollPane.setViewportView(txtRecive);
		panel_2.setLayout(gl_panel_2);

		btsend = new JButton("Enviar");
		btsend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String text = txtMessage.getText();
				String name = txtName.getText();
				if(!text.isEmpty()){
					message = new ChatMessage();
					message.setName(name);
					message.setText(text);
					message.setAction(Action.SEND_ALL);
					service.send(message);
			        txtRecive.append(message.getName() + " diz: " +text + "\n");
				}

				txtMessage.setText("");


			}
		});
		btsend.setEnabled(false);
		btsend.setBounds(133, 274, 117, 25);
		contentPane.add(btsend);
	}
	private class ListenerSocket implements Runnable {

		private ObjectInputStream input;


		public ListenerSocket(Socket socket) {
			try {
				this.input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException ex) {
				Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		@Override
		public void run() {
			ChatMessage message = null;
			try {
				while ((message = (ChatMessage) input.readObject()) != null) {
					Action action = message.getAction();

					if (action.equals(Action.CONNECT)) {
						conected(message);
					} else if (action.equals(Action.DISCONNECT)) {
						disconnect();
						socket.close();
					} else if (action.equals(Action.SEND_ONE)) {
						receive(message);
					} else if (action.equals(Action.USERS_ONLINE)) {
					}
					else if (action.equals(Action.SEND_ALL)) {
						receive(message);

					}



				}
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);

			} catch (IOException ex) {
				Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
			}
		}



	}




	public void conected(ChatMessage message){
		if(message.getText().equals("NO")){
			txtName.setText("");
			JOptionPane.showMessageDialog(null,"Conexão não realizada, tente novamente com outro nome");
			return;
		}
		this.message = message;
		this.btconect.setEnabled(false);
		this.btnSair.setEnabled(true);
		this.txtMessage.setEnabled(true);
		this.btsend.setEnabled(true);
		txtRecive.append(message.getName()+"\n");
		JOptionPane.showMessageDialog(null,"Você está Conectado");
	}

	public void disconnect(){
		this.btconect.setEnabled(true);
		this.txtName.setEnabled(true);
		this.txtMessage.setEnabled(false);
		this.btnSair.setEnabled(false);
	
		JOptionPane.showMessageDialog(null, "Você saiu da sala");
	}

	private void receive(ChatMessage message) {
	        this.txtRecive.append(message.getName() + " diz: " + message.getText() + "\n");
	    }
}
