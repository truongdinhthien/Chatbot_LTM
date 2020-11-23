package Client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Common.Config;

public class MainJframe extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextField textField;
	private static StyledDocument doc;
	private static JButton btnNewButton;
	private static Client clientSocket;
	private static JScrollPane scroll;
	private static JTextPane textPane;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					try {
						final Client clientSocket = new Client(InetAddress.getByName(Config.hostname), Config.PORT);
						if (clientSocket.exchangeSecurityKey() == 0) {
							JOptionPane.showMessageDialog(null, "Lỗi trao đổi key");
							try {
								clientSocket.Close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);
						}
						MainJframe frame = new MainJframe(clientSocket);
						frame.setResizable(false);
						frame.setVisible(true);
						frame.addWindowListener(new java.awt.event.WindowAdapter() {
							@Override
							public void windowClosing(java.awt.event.WindowEvent windowEvent) {
								try {
									clientSocket.SendBye();
									clientSocket.Close();
									System.exit(0);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						waitLoading(frame);
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(null, "Không tìm thấy host. Kiểm tra lại host và port của bạn");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Lỗi kết nối. vui lòng kiểm tra lại host và port của bạn");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public MainJframe(Client clientSocket) throws UnknownHostException, IOException {
		MainJframe.clientSocket = clientSocket;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 639, 522);
		setTitle("Chat Bot Simsimi");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textPane = new JTextPane();
		textPane.setBounds(10, 10, 605, 409);
		textPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textPane.setEditable(false);

		doc = textPane.getStyledDocument();

		scroll = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(10, 10, 605, 409);
		contentPane.add(scroll);

		textField = new JTextField();
		textField.setToolTipText("Nhập chat vào đây ...");
		textField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField.setBounds(10, 429, 506, 46);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Set the text with the key typed
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					getResultActionPerformed();
				}
			}
		});
		btnNewButton = new JButton("Gửi");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getResultActionPerformed();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(530, 429, 85, 46);
		contentPane.add(btnNewButton);
	}

	public static void getResultActionPerformed() {
		try {
			if (!textField.getText().trim().isBlank()) {
				//
				// Set style for client
				SimpleAttributeSet client = new SimpleAttributeSet();
				StyleConstants.setForeground(client, Color.BLUE);
				StyleConstants.setBold(client, true);
				String queryFromClient = textField.getText();
				doc.insertString(doc.getLength(), "Client: ", client);
				doc.insertString(doc.getLength(), queryFromClient + "\n", null);
				// Waiting
				SimpleAttributeSet waiting = new SimpleAttributeSet();
				StyleConstants.setForeground(waiting, Color.GRAY);
				StyleConstants.setItalic(waiting, true);
				String waitingText = "Server is responsing ...";
				doc.insertString(doc.getLength(), waitingText, waiting);

				btnNewButton.setEnabled(false);
				textField.setEditable(false);
				// Execute text
				new Thread(new Runnable() {

					@Override
					public void run() {
						String result;
						try {
							result = clientSocket.Execute(queryFromClient);
							// ClearWait
							doc.remove(doc.getLength() - waitingText.length(), waitingText.length());
							// Set style for server
							SimpleAttributeSet server = new SimpleAttributeSet();
							StyleConstants.setForeground(server, Color.RED);
							StyleConstants.setBold(server, true);
							doc.insertString(doc.getLength(), "Server: ", server);
							doc.insertString(doc.getLength(), result + "\n", null);

							JScrollBar sb = scroll.getVerticalScrollBar();
							sb.setValue(sb.getMaximum());

							btnNewButton.setEnabled(true);
							textField.setEditable(true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				// Reset text field
				textField.setText("");
			}

		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void waitLoading(JFrame frame) {
		final JOptionPane optionPane = new JOptionPane("Bạn đang trong hàng chờ", JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		final JDialog dialog = new JDialog(frame, "Thông báo", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
			}
		});
		dialog.setSize(500, 150);
		dialog.setLocationRelativeTo(frame);
		JButton btnExit = new JButton("Thoát");
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		dialog.add(btnExit);
		new Thread(new Runnable() {
			@Override
			public void run() {
				dialog.setVisible(true);
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (clientSocket.getRespone().equals("waiting")) {
				}
				dialog.setVisible(false);
			}
		}).start();
	}
}
