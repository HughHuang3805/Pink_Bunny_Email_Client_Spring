package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	JTextArea textArea = new JTextArea();
	JPanel emailPanel = new JPanel();
	JPanel passwordPanel = new JPanel();
	JPanel yubikeyPanel = new JPanel();
	JPanel textAreaPanel = new JPanel();;
	JPanel buttonPanel;
	JButton signInButton = new JButton("Sign-in");
	JButton cancelButton = new JButton("Cancel");
	JButton verifyButton = new JButton("Verify");
	JButton nextButton = new JButton("Next");
	JButton sendButton = new JButton("Send");
	JTextField emailText;
	JPasswordField passwordText, yubikeyText;
	JScrollPane jsp;
	JFrame writeFrame;
	JMenu fileMenu, sourceMenu;
	JTextArea emailTextArea;
	JScrollPane jspForBody;
	
	public GUI(){
		setTitle("Pink Bunny E-mail Client");
		setSize(1250, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMenuItems();
		setLocationRelativeTo(null);
		setEmailPanel();
		//getContentPane().setBackground(new Color(51, 102, 255));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	//adds listeners to each of the buttons
	public void setButtonListener(ActionListener a){
		signInButton.addActionListener(a);
		cancelButton.addActionListener(a);
		nextButton.addActionListener(a);
		verifyButton.addActionListener(a);
		sendButton.addActionListener(a);
		for(JMenuItem x : menuItems)
			x.addActionListener(a);
	}

	public void setMenuItems(){
		JMenuBar menuBar = new JMenuBar();
		JMenuItem getNewMessagesItem, writeItem, exitItem, generateKeyPairItem, item5, item6;
		fileMenu = new JMenu("File");
		sourceMenu = new JMenu("Source"); 

		getNewMessagesItem = new JMenuItem("Get New Messages");
		writeItem = new JMenuItem("Write");
		exitItem = new JMenuItem("Exit");
		generateKeyPairItem = new JMenuItem("Generate key pair");

		menuItems.add(getNewMessagesItem);//for adding actionlistener
		menuItems.add(writeItem);
		menuItems.add(exitItem);
		menuItems.add(generateKeyPairItem);

		fileMenu.add(getNewMessagesItem);
		//menu1.addSeparator();
		fileMenu.add(writeItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		//menu1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));

		sourceMenu.add(generateKeyPairItem);

		menuBar.add(fileMenu);
		menuBar.add(sourceMenu);

		fileMenu.setEnabled(false);
		sourceMenu.setEnabled(false);
		setJMenuBar(menuBar);
	}

	public void clearInsertPanel(){
		textAreaPanel.setVisible(false);
	}

	public void setEmailBodyTextArea(){//text area for user to type email content
		setSize(1250, 800);
		if(emailTextArea == null)
			emailTextArea = new JTextArea("Enter your email body ...");
		else
			emailTextArea.setText("Enter your email body ...");
		emailTextArea.setEditable(true);
		emailTextArea.setFont(new Font("Serif", Font.PLAIN, 60));
		jspForBody = new JScrollPane(emailTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		textAreaPanel = new JPanel();
		textAreaPanel.setLayout(new BorderLayout());
		textAreaPanel.add(jspForBody, BorderLayout.CENTER);
		textAreaPanel.repaint();
		sendButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(sendButton);
		buttonPanel.add(cancelButton);
		add(textAreaPanel, BorderLayout.CENTER);
		//add(buttonPanel, BorderLayout.PAGE_END);

		repaint();
		setVisible(true);
	}

	public void setSendDebugTextArea(){
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader("debug.txt"));
			String line = null;
			String debug = "";
			while ((line = br.readLine()) != null) {
				debug = debug + line + "\n";
			}
			emailTextArea.setText(debug);
			emailTextArea.setEditable(false);

			repaint();
			setVisible(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setReceivedEmailTextArea(String message){
		emailTextArea.setText(message);
		emailTextArea.setEditable(false);

		repaint();
		setVisible(true);
	}

	public void setEmailPanel(){//ask for email on emailPanel

		emailPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;

		//Email label and email textfield
		JLabel emailLabel = new JLabel("Email: ");
		emailLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;//position in tje column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.weightx = 1.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 0;
		emailPanel.add(emailLabel, cs);

		emailText = new JTextField(13);
		emailText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		emailPanel.add(emailText, cs);

		emailPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		nextButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);

		//the default button that will be clicked when press "enter"
		getRootPane().setDefaultButton(nextButton);
		add(emailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public void setPasswordPanel(){//ask for password on passwordPanel
		passwordPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;
		//Password label and password textfield
		JLabel passwordLabel = new JLabel("Password: ");
		passwordLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		passwordPanel.add(passwordLabel, cs);

		passwordText = new JPasswordField(13);
		passwordText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		passwordPanel.add(passwordText, cs);

		passwordPanel.setBorder(new LineBorder(Color.GRAY));

		signInButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(signInButton);
		buttonPanel.add(cancelButton);

		getRootPane().setDefaultButton(signInButton);
		add(passwordPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public void setYubikeyPanel(){//ask for yubikey on yubikeyPanel
		passwordPanel.setVisible(false);//fisrt hide password panel
		buttonPanel.setVisible(false);//hide button panel
		buttonPanel.removeAll();//remove whatever is in button panel
		repaint();//repaint the gui
		yubikeyPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;
		//Yubikey label and yubikey password
		JLabel yubikeyLabel = new JLabel("YubiKey: ");
		yubikeyLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;
		cs.gridy = 2;
		cs.gridwidth = 1;
		cs.weightx = 1.0;
		cs.weighty = 0;
		yubikeyPanel.add(yubikeyLabel, cs);

		yubikeyText = new JPasswordField(13);
		yubikeyText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		yubikeyPanel.add(yubikeyText, cs);

		yubikeyPanel.setBorder(new LineBorder(Color.GRAY));

		verifyButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(verifyButton);
		buttonPanel.add(cancelButton);

		getRootPane().setDefaultButton(verifyButton);
		add(yubikeyPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public void setWelcomeScreen(){
		passwordPanel.setVisible(false);
		yubikeyPanel.setVisible(false);
		buttonPanel.setVisible(false);//hide button panel
		buttonPanel.removeAll();//remove whatever is in button panel
		repaint();
		setSize(1250, 800);
		//setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("wallpaper.jpg")))));
		//pack();
		repaint();
		revalidate();

		fileMenu.setEnabled(true);
		sourceMenu.setEnabled(true);
		textAreaPanel = new JPanel();
		setLocationRelativeTo(null);
		setResizable(true);
	}

	public void setWritePanel(){
		writeFrame = new JFrame("New Email");
		writeFrame.setSize(1000, 800);
		writeFrame.setVisible(true);
		JTextArea emailTextArea;
		JScrollPane jspForBody;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints

		JLabel senderLabel = new JLabel("Sender: ");
		cs.gridx = 0;//position in the column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.EAST;
		cs.fill = GridBagConstraints.NONE;
		panel.add(senderLabel, cs);

		JTextField senderTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.NONE;
		//headerPanel.add(senderTextField, cs);
		//headerPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel
		panel.add(senderTextField, cs);
		
		JLabel recipientLabel = new JLabel("Recipient: ");
		cs.gridx = 0;//position in the column
		cs.gridy = 1;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.EAST;
		cs.fill = GridBagConstraints.NONE;
		panel.add(recipientLabel, cs);

		JTextField recipientTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.NONE;
		panel.add(recipientTextField, cs);

		emailTextArea = new JTextArea("Enter your email body ...", 15, 40);
		emailTextArea.setEditable(true);
		emailTextArea.setFont(new Font("Serif", Font.PLAIN, 30));
		emailTextArea.setLineWrap(true);
		jspForBody = new JScrollPane(emailTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cs.gridx = 0;
		cs.gridy = 2;
		//cs.gridwidth = 1;
		//cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.fill = GridBagConstraints.NONE;
		panel.add(jspForBody, cs);

		sendButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendButton);
		buttonPanel.add(cancelButton);
		cs.gridx = 0;
		cs.gridy = 3;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttonPanel, cs);
		
		writeFrame.add(panel);
		writeFrame.setResizable(false);
		writeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		writeFrame.pack();
		writeFrame.repaint();
		writeFrame.setLocationRelativeTo(null);
		writeFrame.setVisible(true);

	}

	public String getEmail(){
		//get rid of any space in the username
		return emailText.getText().replaceAll("\\s+","");
	}

	public String getPassword(){
		String passText = new String(passwordText.getPassword());
		return passText;
	}

	public String getYubikey(){
		String passText = new String(yubikeyText.getPassword());
		return passText;
	}

	public void clearGUI(){
		textArea.setText(null);
	}

}
