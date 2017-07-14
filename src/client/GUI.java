package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	Vector<JMenu> menus = new Vector<JMenu>();
	JTextArea textArea = new JTextArea();
	JPanel emailPanel;
	JPanel passwordPanel;
	JPanel yubikeyPanel;
	JPanel textAreaPanel = new JPanel();
	JPanel buttonPanel;
	JButton signInButton = new JButton("Sign-in");
	JButton loginButton = new JButton("Log-in");
	JButton cancelButton = new JButton("Cancel");
	JButton verifyButton = new JButton("Verify");
	JButton nextButton = new JButton("Next");
	JButton sendButton = new JButton("Send");
	JButton discardButton = new JButton("Discard");
	JButton secureDiscardButton = new JButton("Secure Discard");
	JTextField emailTextField, senderTextField, recipientTextField, subjectTextField;
	JTextField secureEmailTextField, secureSenderTextField, secureRecipientTextField, secureSubjectTextField;
	JPasswordField passwordText, yubikeyText;
	JScrollPane jsp;
	JFrame writeFrame, secureWriteFrame, addAccountFrame;
	JMenu fileMenu = new JMenu("File");
	JMenu toolMenu = new JMenu("Source");
	JTextArea emailContentText;
	JTextArea secureEmailContentText;
	JScrollPane jspForBody;
	JScrollPane secureJSPForBody;
	JComboBox<String> emailList;
	String imageFileName = "icons/favicon.png";
	Vector<JTree> trees = new Vector<JTree>();
	JPanel mainPanel, leftPanel, rightPanel, emailsPanel, emailInboxPanel, previewPanel;
	JPopupMenu emailPopupMenu = new JPopupMenu();
	JTable emailTable;

	public GUI(MouseListener a, ActionListener b, Vector<String> userEmails) throws Exception{
		setTitle("Pink Bunny E-mail Client");
		setSize(1250, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMenuItems();
		setMenuInsets();
		setLocationRelativeTo(null);
		//getContentPane().setBackground(new Color(51, 102, 255));
		setLocationRelativeTo(null);
		ImageIcon img = new ImageIcon(imageFileName);
		setIconImage(img.getImage());
		setPopupItems(b);
		//addMouseListener(a);
		setMainPanel(userEmails, a);
		setVisible(true);
	}

	//adds listeners to each of the buttons
	public void setButtonListener(ActionListener a){
		signInButton.addActionListener(a);
		loginButton.addActionListener(a);
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
		//writeItem, secureWriteItem,
		JMenuItem getAllNewMessagesItem, exitItem, generateKeyPairItem, addNewAccountItem;
		menus.add(fileMenu);
		menus.add(toolMenu);

		addNewAccountItem = new JMenuItem("Add New Account");
		addNewAccountItem.setIcon(new ImageIcon("icons/addicon.png"));
		getAllNewMessagesItem = new JMenuItem("Get All New Messages");
		/*writeItem = new JMenuItem("Write");
		writeItem.setToolTipText("Send Emails Un-Encrypted");
		secureWriteItem = new JMenuItem("Secure Write");
		secureWriteItem.setToolTipText("Send Emails Encrypted");*/
		exitItem = new JMenuItem("Exit");
		generateKeyPairItem = new JMenuItem("Generate Key Pair");
		generateKeyPairItem.setIcon(new ImageIcon("icons/keyicon.png"));

		//add items to a list for adding actionlistener
		//menuItems.add(writeItem);
		menuItems.add(getAllNewMessagesItem);
		menuItems.add(exitItem);
		menuItems.add(generateKeyPairItem);
		//menuItems.add(secureWriteItem);
		menuItems.add(addNewAccountItem);

		fileMenu.add(addNewAccountItem);
		//fileMenu.add(secureWriteItem);
		//secureWriteItem.setEnabled(false);
		//fileMenu.add(writeItem);
		//writeItem.setEnabled(false);
		fileMenu.add(getAllNewMessagesItem);
		getAllNewMessagesItem.setEnabled(false);
		//menu1.addSeparator();
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		//menu1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));

		toolMenu.add(generateKeyPairItem);

		menuBar.add(fileMenu);
		menuBar.add(toolMenu);

		fileMenu.setEnabled(true);
		toolMenu.setEnabled(false);
		setJMenuBar(menuBar);
	}

	public void setMenuInsets(){//add a little margin in between each email account in the left panel
		for(JMenu x : menus)
			x.setMargin(new Insets(0,3,0,3));
	}

	public void enableAllMenuItems(){
		for(JMenuItem x : menuItems)
			x.setEnabled(true);
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

	public void setMainPanel(Vector<String> userEmails, MouseListener a) throws Exception{
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		emailsPanel = new JPanel();
		emailInboxPanel = new JPanel();
		previewPanel = new JPanel();

		mainPanel.setLayout(new GridLayout());
		leftPanel.setLayout(new GridBagLayout());
		rightPanel.setLayout(new GridLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);//split the middle
		setEmailJTreePanel(userEmails, a);//for left panel email lists
		//leftPanel.add(jTree);

		leftPanel.setBorder(new LineBorder(Color.GRAY));
		rightPanel.setBorder(new LineBorder(Color.GRAY));

		splitPane.setLeftComponent(new JScrollPane(leftPanel));//left component in the split pane is the left panel
		splitPane.setRightComponent(rightPanel);//right component in the split pane is the right panel
		splitPane.setDividerSize(2);
		//splitPane.setDividerLocation(0.75);
		splitPane.setResizeWeight(0.01);
		mainPanel.add(splitPane);

		add(mainPanel);
		repaint();
		revalidate();

		fileMenu.setEnabled(true);
		toolMenu.setEnabled(true);
		textAreaPanel = new JPanel();
		setLocationRelativeTo(null);
		setResizable(true);
	}

	public void setEmailJTreePanel(Vector<String> userEmails, MouseListener a){

		UIManager.put("Tree.expandedIcon",  new ImageIcon("icons/clapsedicon.png"));//changes the expand icon
		UIManager.put("Tree.collapsedIcon", new ImageIcon("icons/expandicon.png"));//changes the clapsed icon
		for(int i = 0; i < userEmails.size(); i++){
			DefaultMutableTreeNode emailRoot = new DefaultMutableTreeNode(userEmails.elementAt(i));
			trees.add(new JTree(emailRoot));
			trees.elementAt(i).setShowsRootHandles(true);

			DefaultMutableTreeNode inboxLeaf = new DefaultMutableTreeNode("Inbox");
			DefaultMutableTreeNode writeLeaf = new DefaultMutableTreeNode("Write");
			DefaultMutableTreeNode secureWriteLeaf = new DefaultMutableTreeNode("Secure write");
			DefaultMutableTreeNode draftLeaf = new DefaultMutableTreeNode("Draft");
			DefaultMutableTreeNode sentLeaf = new DefaultMutableTreeNode("Sent");
			DefaultMutableTreeNode spamLeaf = new DefaultMutableTreeNode("Spam");
			emailRoot.add(inboxLeaf);
			emailRoot.add(writeLeaf);
			emailRoot.add(secureWriteLeaf);
			emailRoot.add(draftLeaf);
			emailRoot.add(sentLeaf);
			emailRoot.add(spamLeaf);
			trees.elementAt(i).setCellRenderer(new TreeRenderer());
			trees.elementAt(i).setRowHeight(23);//gap between each email
			trees.elementAt(i).addMouseListener(a);
			Font currentFont = trees.elementAt(i).getFont();
			//font size of the displaying email list
			trees.elementAt(i).setFont(new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 3));

			GridBagConstraints cs = new GridBagConstraints();//constraints
			cs.fill = GridBagConstraints.BOTH;
			cs.anchor = GridBagConstraints.NORTH;
			cs.gridx = 0;//position in the column
			cs.gridy = i;//position in the row
			cs.gridwidth = 1;
			leftPanel.add(trees.elementAt(i), cs);
		}
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.BOTH;
		cs.gridy = userEmails.size() + 1;
		cs.weightx = 1;
		cs.weighty = 1;
		JPanel filler = new JPanel();
		filler.setOpaque(false);
		leftPanel.add(filler, cs);
		leftPanel.setBackground(Color.white);
	}

	public void setDisplayPanel(SecureMailService mailServer) throws Exception{
		if(mailServer.getEmailTable() == null){
			String[] columnNames = {"Subject", "From", "Date", "Read"};
			Message[] messages = mailServer.getMessages();
			String[][] data = new String[messages.length][4] ;
			for (int i = messages.length - 1; i >= 0 ; i--) {  
				Message message = messages[i];  
				Scanner s;
				s = new Scanner(message.getInputStream()).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : " ";

				data[(messages.length - 1) - i][0] = message.getSubject();
				data[(messages.length - 1) - i][1] = InternetAddress.toString(message.getFrom());
				data[(messages.length - 1) - i][2] = message.getReceivedDate().toString();
			}  
			mailServer.setEmailTable(new JTable(data, columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				};
			});
			mailServer.getEmailTable().setFillsViewportHeight(true);
		}
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(mailServer.getEmailTable()));
		repaint();
		revalidate();
	}

	public void setReceivedEmailTextArea(String message){
		emailContentText.setText(message);
		emailContentText.setEditable(false);

		repaint();
		setVisible(true);
	}

	public void setAddAccountFrame(){//ask for email on emailPanel
		addAccountFrame = null;
		addAccountFrame = new JFrame("Email");
		emailPanel = new JPanel();
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
		addAccountFrame.getRootPane().setDefaultButton(nextButton);
		addAccountFrame.add(emailPanel, BorderLayout.CENTER);
		addAccountFrame.add(buttonPanel, BorderLayout.PAGE_END);
		addAccountFrame.pack(); //let layout managers in charge of the frame size
		ImageIcon img = new ImageIcon(imageFileName);
		addAccountFrame.setIconImage(img.getImage());
		addAccountFrame.setResizable(false);
		addAccountFrame.setVisible(true);
		addAccountFrame.setLocationRelativeTo(this);
		//add(emailFrame);
	}

	public void setLoginFrame(SecureMailService emailServer){//ask for email on emailPanel

		emailServer.setLoginFrame(new JFrame("Log In"));
		JFrame loginFrame = emailServer.getLoginFrame();
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;

		//Email label and email textfield
		JLabel emailLabel = new JLabel("Password: ");
		emailLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;//position in tje column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.weightx = 1.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 0;
		passwordPanel.add(emailLabel, cs);

		passwordText = new JPasswordField(13);
		passwordText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		passwordPanel.add(passwordText, cs);

		passwordPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		loginButton.setFont(new Font("Serif", Font.PLAIN, 25));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 25));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(loginButton);
		buttonPanel.add(cancelButton);

		//the default button that will be clicked when press "enter"
		loginFrame.getRootPane().setDefaultButton(loginButton);
		loginFrame.add(passwordPanel, BorderLayout.CENTER);
		loginFrame.add(buttonPanel, BorderLayout.PAGE_END);
		loginFrame.pack(); //let layout managers in charge of the frame size
		ImageIcon img = new ImageIcon(imageFileName);
		loginFrame.setIconImage(img.getImage());
		loginFrame.setResizable(false);
		loginFrame.setVisible(true);
		loginFrame.setLocationRelativeTo(this);
		//add(emailFrame);
	}

	public void setPasswordPanel(){//ask for password on passwordPanel
		passwordPanel = new JPanel();
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

		addAccountFrame.getRootPane().setDefaultButton(signInButton);
		addAccountFrame.add(passwordPanel, BorderLayout.CENTER);
		addAccountFrame.add(buttonPanel, BorderLayout.PAGE_END);

		addAccountFrame.pack(); //let layout managers in charge of the frame size
		addAccountFrame.setResizable(false);
		addAccountFrame.setVisible(true);
	}

	public void setYubikeyPanel(SecureMailService emailServer){//ask for yubikey on yubikeyPanel


		JFrame loginFrame = emailServer.getLoginFrame();yubikeyPanel = new JPanel();
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

		loginFrame.getRootPane().setDefaultButton(verifyButton);
		loginFrame.add(yubikeyPanel, BorderLayout.CENTER);
		loginFrame.add(buttonPanel, BorderLayout.PAGE_END);

		loginFrame.pack(); //let layout managers in charge of the frame size
		loginFrame.setResizable(false);
		loginFrame.setVisible(true);
	}

	public void setWriteFrame(Vector<String> userEmails, SecureMailService emailServer){//write email

		emailList = new JComboBox<>(userEmails);

		emailServer.setWriteFrame(new JFrame("Write: New Email"));
		JFrame writeFrame = emailServer.getWriteFrame();
		writeFrame.setSize(1000, 800);
		ImageIcon img = new ImageIcon(imageFileName);
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

		/*senderTextField = new JTextField(13);
		senderTextField.setText(getEmail());
		senderTextField.setEditable(false);*/
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(emailList, cs);

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
		writeFrame.setLocationRelativeTo(this);
		writeFrame.setVisible(true);
	}

	public void setSecureWritePanel(Vector<String> userEmails, SecureMailService emailServer, ActionListener a){

		JComboBox<String> emailList = new JComboBox<>(userEmails);
		//System.out.println(emailList.getSelectedItem());

		emailServer.setSecureWriteFrame(new JFrame("Secure Write: New Email"));
		JFrame secureWriteFrame = emailServer.getSecureWriteFrame();
		secureWriteFrame.setSize(1000, 800);
		ImageIcon img = new ImageIcon(imageFileName);
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

		/*secureSenderTextField = new JTextField(13);
		secureSenderTextField.setText(this.getEmail());
		secureSenderTextField.setEditable(false);*/
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.anchor = GridBagConstraints.WEST;
		cs.fill = GridBagConstraints.HORIZONTAL;
		writePanel.add(emailList, cs);

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

		JTextField secureRecipientTextField = new JTextField(13);
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

		JTextField secureSubjectTextField = new JTextField(13);
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

		JTextArea secureEmailContentText = new JTextArea("Enter your email body ...", 15, 25);//email content
		secureEmailContentText.setEditable(true);
		secureEmailContentText.setFont(new Font("Serif", Font.PLAIN, 30));
		secureEmailContentText.setLineWrap(true);
		JScrollPane secureJSPForBody = new JScrollPane(secureEmailContentText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cs.gridx = 0;
		cs.gridy = 3;
		//cs.gridwidth = 1;
		//cs.gridheight = 1;
		cs.weightx = 100.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 100.0;
		cs.insets = new Insets(5, 5, 5, 5);
		cs.fill = GridBagConstraints.NONE;
		writePanel.add(secureJSPForBody, cs);

		JButton secureSendButton = new JButton("Secure Send");
		JButton secureDiscardButton = new JButton("Secure Discard");
		secureSendButton.addActionListener(a);
		secureDiscardButton.addActionListener(a);
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
		secureWriteFrame.setLocationRelativeTo(this);
		secureWriteFrame.setVisible(true);
	}

	public void setPopupItems(ActionListener a){
		JMenuItem getMessageItem = new JMenuItem("Get new messages");
		getMessageItem.addActionListener(a);
		JMenuItem loginItem = new JMenuItem("Log in");
		loginItem.addActionListener(a);
		//getMessageItem.setActionCommand("Get New Messages");
		emailPopupMenu.add(getMessageItem);
		emailPopupMenu.add(loginItem);
	}

	public String getEmailFromCombobox(){
		//get rid of any space in the username
		return ((String) emailList.getSelectedItem()).replaceAll("\\s+", "");
	}

	public String getEmail(){
		return emailTextField.getText().replaceAll("\\s+", "");
	}

	public String getPassword(){
		if(passwordText != null){
			String passText = new String(passwordText.getPassword());
			return passText;
		}
		return null;
	}

	public String getYubikey(){
		String passText = new String(yubikeyText.getPassword());
		return passText;
	}

	public void clearGUI(){
		textArea.setText(null);
	}

	public String getRecipient() {
		if(recipientTextField != null){
			return recipientTextField.getText().replaceAll("\\s+", "");
		}
		return null;
	}

	public String getSecureRecipient() {
		if(secureRecipientTextField != null){
			return secureRecipientTextField.getText().replaceAll("\\s+", "");
		}
		return null;
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
