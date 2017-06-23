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

import javax.swing.ImageIcon;
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
	JButton secureSendButton = new JButton("Secure Send");
	JButton discardButton = new JButton("Discard");
	JButton secureDiscardButton = new JButton("Secure Discard");
	JTextField emailTextField, senderTextField, recipientTextField, subjectTextField;
	JTextField secureEmailTextField, secureSenderTextField, secureRecipientTextField, secureSubjectTextField;
	JPasswordField passwordText, yubikeyText;
	JScrollPane jsp;
	JFrame writeFrame, secureWriteFrame;
	JMenu fileMenu, sourceMenu;
	JTextArea emailContentText;
	JTextArea secureEmailContentText;
	JScrollPane jspForBody;
	JScrollPane secureJSPForBody;
	
	public GUI(){
		setTitle("Pink Bunny E-mail Client");
		setSize(1250, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMenuItems();
		setLocationRelativeTo(null);
		setEmailPanel();
		//getContentPane().setBackground(new Color(51, 102, 255));
		setLocationRelativeTo(null);
		ImageIcon img = new ImageIcon("favicon.png");
		setIconImage(img.getImage());
		setVisible(true);
	}

	//adds listeners to each of the buttons
	public void setButtonListener(ActionListener a){
		signInButton.addActionListener(a);
		cancelButton.addActionListener(a);
		nextButton.addActionListener(a);
		verifyButton.addActionListener(a);
		sendButton.addActionListener(a);
		secureSendButton.addActionListener(a);
		discardButton.addActionListener(a);
		secureDiscardButton.addActionListener(a);
		for(JMenuItem x : menuItems)
			x.addActionListener(a);
	}

	public void setMenuItems(){
		JMenuBar menuBar = new JMenuBar();
		JMenuItem getNewMessagesItem, writeItem, exitItem, generateKeyPairItem, secureWriteItem, item6;
		fileMenu = new JMenu("File");
		sourceMenu = new JMenu("Source"); 

		getNewMessagesItem = new JMenuItem("Get New Messages");
		writeItem = new JMenuItem("Write");
		writeItem.setToolTipText("Send Emails Un-Encrypted");
		secureWriteItem = new JMenuItem("Secure Write");
		secureWriteItem.setToolTipText("Send Emails Encrypted");
		exitItem = new JMenuItem("Exit");
		generateKeyPairItem = new JMenuItem("Generate Key Pair");

		//add items to a list for adding actionlistener
		menuItems.add(writeItem);
		menuItems.add(getNewMessagesItem);
		menuItems.add(exitItem);
		menuItems.add(generateKeyPairItem);
		menuItems.add(secureWriteItem);

		fileMenu.add(secureWriteItem);
		fileMenu.add(writeItem);
		fileMenu.add(getNewMessagesItem);
		//menu1.addSeparator();
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

	public void setSendDebugTextArea(){
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader("debug.txt"));
			String line = null;
			String debug = "";
			while ((line = br.readLine()) != null) {
				debug = debug + line + "\n";
			}
			emailContentText.setText(debug);
			emailContentText.setEditable(false);

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

	public void setSecureSendDebugTextArea(){
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader("debug.txt"));
			String line = null;
			String debug = "";
			while ((line = br.readLine()) != null) {
				debug = debug + line + "\n";
			}
			secureEmailContentText.setText(debug);
			secureEmailContentText.setEditable(false);

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
		emailContentText.setText(message);
		emailContentText.setEditable(false);

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

		emailTextField = new JTextField(13);
		emailTextField.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		emailPanel.add(emailTextField, cs);

		emailPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		nextButton.setFont(new Font("Serif", Font.PLAIN, 25));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 25));

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

		signInButton.setFont(new Font("Serif", Font.PLAIN, 25));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 25));

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

		verifyButton.setFont(new Font("Serif", Font.PLAIN, 25));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 25));

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

	public void setWritePanel(){//write email
		writeFrame = new JFrame("Write: New Email");
		writeFrame.setSize(1000, 800);
		ImageIcon img = new ImageIcon("favicon.png");
		writeFrame.setIconImage(img.getImage());
		writeFrame.setVisible(true);
		
		JPanel mainPanel = new JPanel();
		JPanel writePanel = new JPanel();
		writePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints cs = new GridBagConstraints();//constraints

		JLabel senderLabel = new JLabel("Sender: ");//sender
		cs.gridx = 0;//position in the column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.NONE;
		writePanel.add(senderLabel, cs);

		senderTextField = new JTextField(13);
		senderTextField.setText(this.getEmail());
		senderTextField.setEditable(false);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(senderTextField, cs);
		
		JLabel recipientLabel = new JLabel("Recipient: ");//recipient
		cs.gridx = 0;//position in the column
		cs.gridy = 1;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(recipientLabel, cs);

		recipientTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(recipientTextField, cs);
		
		JLabel subjectLabel = new JLabel("Subject: ");//subject
		cs.gridx = 0;//position in the column
		cs.gridy = 2;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(subjectLabel, cs);

		subjectTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(subjectTextField, cs);

		emailContentText = new JTextArea("Enter your email body ...", 15, 25);//email content
		emailContentText.setEditable(true);
		emailContentText.setFont(new Font("Serif", Font.PLAIN, 30));
		emailContentText.setLineWrap(true);
		jspForBody = new JScrollPane(emailContentText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cs.gridx = 0;
		cs.gridy = 3;
		//cs.gridwidth = 1;
		//cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.fill = GridBagConstraints.NONE;
		writePanel.add(jspForBody, cs);

		sendButton.setFont(new Font("Serif", Font.PLAIN, 25));
		discardButton.setFont(new Font("Serif", Font.PLAIN, 25));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendButton);
		buttonPanel.add(discardButton);
		cs.gridx = 0;
		cs.gridy = 4;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(buttonPanel, cs);
		
		mainPanel.add(writePanel);
		writeFrame.add(mainPanel);
		writeFrame.setResizable(false);
		writeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		writeFrame.pack();
		writeFrame.repaint();
		writeFrame.setLocationRelativeTo(null);
		writeFrame.setVisible(true);
	}

	public void setSecureWritePanel(){
		secureWriteFrame = new JFrame("Secure Write: New Email");
		secureWriteFrame.setSize(1000, 800);
		ImageIcon img = new ImageIcon("favicon.png");
		secureWriteFrame.setIconImage(img.getImage());
		secureWriteFrame.setVisible(true);
		
		JPanel mainPanel = new JPanel();
		JPanel writePanel = new JPanel();
		writePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints cs = new GridBagConstraints();//constraints

		JLabel senderLabel = new JLabel("Sender: ");//sender
		cs.gridx = 0;//position in the column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.NONE;
		writePanel.add(senderLabel, cs);

		secureSenderTextField = new JTextField(13);
		secureSenderTextField.setText(this.getEmail());
		secureSenderTextField.setEditable(false);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(secureSenderTextField, cs);
		
		JLabel recipientLabel = new JLabel("Recipient: ");//recipient
		cs.gridx = 0;//position in the column
		cs.gridy = 1;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(recipientLabel, cs);

		secureRecipientTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(secureRecipientTextField, cs);
		
		JLabel subjectLabel = new JLabel("Subject: ");//subject
		cs.gridx = 0;//position in the column
		cs.gridy = 2;//position in the row
		cs.gridwidth = 1;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(subjectLabel, cs);

		secureSubjectTextField = new JTextField(13);
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(secureSubjectTextField, cs);

		secureEmailContentText = new JTextArea("Enter your email body ...", 15, 25);//email content
		secureEmailContentText.setEditable(true);
		secureEmailContentText.setFont(new Font("Serif", Font.PLAIN, 30));
		secureEmailContentText.setLineWrap(true);
		secureJSPForBody = new JScrollPane(secureEmailContentText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cs.gridx = 0;
		cs.gridy = 3;
		//cs.gridwidth = 1;
		//cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.fill = GridBagConstraints.NONE;
		writePanel.add(secureJSPForBody, cs);

		secureSendButton.setFont(new Font("Serif", Font.PLAIN, 25));
		secureDiscardButton.setFont(new Font("Serif", Font.PLAIN, 25));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(secureSendButton);
		buttonPanel.add(secureDiscardButton);
		cs.gridx = 0;
		cs.gridy = 4;
		cs.gridwidth = 3;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(buttonPanel, cs);
		
		mainPanel.add(writePanel);
		secureWriteFrame.add(mainPanel);
		secureWriteFrame.setResizable(false);
		secureWriteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		secureWriteFrame.pack();
		secureWriteFrame.repaint();
		secureWriteFrame.setLocationRelativeTo(null);
		secureWriteFrame.setVisible(true);
	}
	
	public String getEmail(){
		//get rid of any space in the username
		return emailTextField.getText().replaceAll("\\s+","");
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

	public String getRecipient() {
		return recipientTextField.getText();
	}
	
	public String getSecureRecipient() {
		return secureRecipientTextField.getText();
	}

	public void setRecipient(JTextField recipientTextField) {
		this.recipientTextField = recipientTextField;
	}

	public void setSecureRecipient(JTextField secureRecipientTextField) {
		this.secureRecipientTextField = secureRecipientTextField;
	}
	
	public String getSubject() {
		return subjectTextField.getText();
	}

	public String getSecureSubject() {
		return secureSubjectTextField.getText();
	}
	
	public void setSubject(JTextField subjectTextField) {
		this.subjectTextField = subjectTextField;
	}
	
	public void setSecureSubject(JTextField secureSubjectTextField) {
		this.secureSubjectTextField = secureSubjectTextField;
	}
	
	public JPanel getEmailPanel() {
		return emailPanel;
	}

	public void setEmailPanel(JPanel emailPanel) {
		this.emailPanel = emailPanel;
	}

	public String getEmailContentText() {
		return emailContentText.getText();
	}

	public void setEmailContentText(JTextArea emailContentText) {
		this.emailContentText = emailContentText;
	}

	public String getSecureEmailContentText() {
		return secureEmailContentText.getText();
	}

	public void setSecureEmailContentText(JTextArea secureEmailContentText) {
		this.secureEmailContentText = secureEmailContentText;
	}
}
