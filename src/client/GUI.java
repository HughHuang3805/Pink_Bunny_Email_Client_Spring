package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
import javax.swing.JOptionPane;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	Vector<JMenu> menus = new Vector<JMenu>();
	JPanel emailPanel, passwordPanel, yubikeyPanel, buttonPanel;
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
	MouseListener a;
	ActionListener b;
	JSplitPane splitPane;

	public GUI(MouseListener a, ActionListener b, Vector<String> userEmails) throws Exception{
		setTitle("Pink Bunny E-mail Client");
		setSize(1250, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setActionListener(b);
		setMouseListener(a);
		setMenuItems();
		setMenuInsets();
		setLocationRelativeTo(null);
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
		/*signInButton.addActionListener(a);
		loginButton.addActionListener(a);
		cancelButton.addActionListener(a);
		nextButton.addActionListener(a);
		verifyButton.addActionListener(a);
		sendButton.addActionListener(a);
		//secureSendButton.addActionListener(a);
		discardButton.addActionListener(a);
		secureDiscardButton.addActionListener(a);*/
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

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);//split the middle
		setEmailJTreeLeftPanel(userEmails, a);//for left panel email lists
		//leftPanel.add(jTree);

		leftPanel.setBorder(new LineBorder(Color.BLACK));
		rightPanel.setBorder(new LineBorder(Color.BLACK));

		JScrollPane leftPanelScrollPane = new JScrollPane(leftPanel);
		leftPanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);//scrolling speed
		splitPane.setLeftComponent(leftPanelScrollPane);//left component in the split pane is the left panel
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
		setLocationRelativeTo(null);
		setResizable(true);
	}

	public void setEmailJTreeLeftPanel(Vector<String> userEmails, MouseListener a){

		UIManager.put("Tree.expandedIcon",  new ImageIcon("icons/clapsedicon.png"));//changes the expand icon
		UIManager.put("Tree.collapsedIcon", new ImageIcon("icons/expandicon.png"));//changes the clapsed icon
		leftPanel.removeAll();
		trees.removeAllElements();
		for(int i = 0; i < userEmails.size(); i++){
			DefaultMutableTreeNode emailRoot = new DefaultMutableTreeNode(userEmails.elementAt(i));
			trees.add(new JTree(emailRoot));
			JTree tree = trees.elementAt(i);
			tree.setShowsRootHandles(true);

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


			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setCellRenderer(new TreeRenderer());
			tree.setRowHeight(23);//gap between each email
			tree.addMouseListener(a);
			tree.addTreeSelectionListener(new TreeSelectionListener(){

				@Override
				public void valueChanged(TreeSelectionEvent arg0) {
					// TODO Auto-generated method stub
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
							tree.getLastSelectedPathComponent();
					if(node == null) return;
					if(node.toString() == "Inbox"){
						SecureMailService emailServer = GUIController.emailObjectMap.get(node.getRoot().toString());
						//System.out.println("email server is null" + emailServer == null);
						if(emailServer.isSmtpLoggedIn()){
							try {
								setDisplayRightPanel(emailServer);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//System.out.println(emailServer.getEmailTable() == null);
						} else {
							JOptionPane.showMessageDialog(null, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
							//myGui.setLoginFrame(emailServer);
						}
						tree.clearSelection();
					}
					if(node.toString() == "Secure write"){
						SecureMailService emailServer = GUIController.emailObjectMap.get(node.getRoot().toString());
						if(emailServer.isSmtpLoggedIn()){
							setSecureWritePanel(userEmails, emailServer);
						} else{
							JOptionPane.showMessageDialog(null, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
							//myGui.setLoginFrame(emailServer);
						}
						//System.out.println("double clicked");
						tree.clearSelection();
					} else if(node.toString() == "Write"){
						SecureMailService emailServer = GUIController.emailObjectMap.get(node.getRoot().toString());
						if(emailServer.isSmtpLoggedIn()){
							setWriteFrame(userEmails, emailServer);
						} else{
							JOptionPane.showMessageDialog(null, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
							//myGui.setLoginFrame(emailServer);
						}
						//System.out.println("double clicked");
						tree.clearSelection();
					}
				}

			});

			Font currentFont = tree.getFont();
			//font size of the displaying email list
			tree.setFont(new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 3));

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
		leftPanel.repaint();
		leftPanel.revalidate();
		leftPanel.setBackground(Color.white);

		//splitPane.remove(leftPanel);
		JScrollPane leftPanelScrollPane = new JScrollPane(leftPanel);
		leftPanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);//scrolling speed
		splitPane.setLeftComponent(leftPanelScrollPane);//left component in the split pane is the left panel
		splitPane.setRightComponent(rightPanel);//right component in the split pane is the right panel
		splitPane.setDividerSize(2);
		//splitPane.setDividerLocation(0.75);
		splitPane.setResizeWeight(0.01);

		repaint();
		revalidate();
	}

	@SuppressWarnings({ "serial" })
	public void setDisplayRightPanel(SecureMailService emailServer) throws Exception{

		if(emailServer.getEmailTable() == null){
			JTable emailTable; 
			String[] columnNames = {"Subject", "From", "Date", "Read"};
			Message[] messages = emailServer.getMessages();
			String[][] data = new String[messages.length][4] ;
			//messages.length - 1
			for (int i = messages.length - 1; i >= 0 ; i--) {  
				Message message = messages[i];  

				data[(messages.length - 1) - i][0] = message.getSubject();
				ByteBuffer bb = ByteBuffer.wrap(InternetAddress.toString(message.getFrom()).getBytes());
				//Charset.forName("UTF-8").decode(bb).toString();
				//String address = new String(message.getFrom().toString().getBytes());
				data[(messages.length - 1) - i][1] = Charset.forName("UTF-8").decode(bb).toString();
				data[(messages.length - 1) - i][2] = message.getReceivedDate().toString();
			}  
			emailTable = new JTable(data, columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				};
			};
			emailServer.setEmailTable(emailTable);
			emailTable.getTableHeader().setFont(new Font("Serif", Font.BOLD, 20));
			TableCellRenderer rendererFromHeader = emailTable.getTableHeader().getDefaultRenderer();
			JLabel headerLabel = (JLabel) rendererFromHeader;
			//headerLabel.setHorizontalAlignment(JLabel.CENTER);//center header text
			emailTable.setFillsViewportHeight(true);
			emailTable.setFont(new Font("Serif", Font.PLAIN, 14));
			emailTable.setRowHeight(20);
			emailTable.setAutoCreateRowSorter(true);
			/*emailTable.getModel().addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					System.out.println("hi");
				}
			});*/
			emailTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if(e.getValueIsAdjusting() == false){//this makes the event go once
						int row = emailTable.getSelectedRow();
						System.out.println(row);
						try {
							emailServer.getEmailByNumber(row);
							rightPanel.removeAll();
							rightPanel.add(emailServer.getRightEmailContentPanel());
							repaint();
							revalidate();
							return;
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
		}
		rightPanel.removeAll();
		rightPanel.add(new JScrollPane(emailServer.getEmailTable()));
		repaint();
		revalidate();
	}

	public void setReceivedEmailTextArea(String message){
		emailContentText.setText(message);
		emailContentText.setEditable(false);

		repaint();
		setVisible(true);
	}

	public void setAddAccountEmailFrame(){//ask for email on emailPanel

		JFrame emailFrame = new JFrame("Email");
		JPanel emailPanel = new JPanel();
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

		JTextField emailTextField = new JTextField(13);
		emailTextField.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		emailPanel.add(emailTextField, cs);

		emailPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		JButton nextButton = new JButton("Next");
		JButton cancelButton = new JButton("Cancel");
		nextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String email = emailTextField.getText();//get the email
				SecureMailService emailServer = new SecureMailService();
				if(email != null && !email.equals("")){
					String emailType = email.substring(email.indexOf("@") + 1, email.indexOf(".")).toUpperCase();//see what kind of host the user is using
					String smtpServer = GUIController.smtpServers.get(emailType.toUpperCase());//check what smtp server it is using for that host
					String portNumber = GUIController.portNumbers.get(emailType.toUpperCase());//check what port it is using for that host
					String imapServer = GUIController.imapServers.get(emailType.toUpperCase());
					if(smtpServer != null & portNumber != null){
						emailServer.setSMTPServer(smtpServer);//set smtp server
						emailServer.setPort(Integer.parseInt(portNumber));//set port number
						emailServer.setUsername(email);//set user email
						emailServer.setImapHost(imapServer);
						emailServer.setEmailType(emailType);
						GUIController.emailObjectMap.put(email, emailServer);

						Properties prop = new Properties();
						OutputStream output = null;
						try {
							prop.load(new FileInputStream("userconfig.properties"));
							output = new FileOutputStream("userconfig.properties");
							GUIController.counter++;
							prop.setProperty("email" + GUIController.counter, emailServer.getUsername());
							prop.store(output, null);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						setAddAccountPasswordFrame(emailServer);
						emailFrame.dispose();
					}
				}
			}
		});
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				emailFrame.dispose();
			}

		});
		nextButton.setFont(new Font("Serif", Font.PLAIN, 23));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 23));

		JPanel buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);

		//the default button that will be clicked when press "enter"
		emailFrame.getRootPane().setDefaultButton(nextButton);
		emailFrame.add(emailPanel, BorderLayout.CENTER);
		emailFrame.add(buttonPanel, BorderLayout.PAGE_END);
		emailFrame.pack(); //let layout managers in charge of the frame size
		ImageIcon img = new ImageIcon(imageFileName);
		emailFrame.setIconImage(img.getImage());
		emailFrame.setResizable(false);
		emailFrame.setVisible(true);
		emailFrame.setLocationRelativeTo(this);
		//add(emailFrame);
	}

	public void setAddAccountPasswordFrame(SecureMailService emailServer){//ask for password on passwordPanel
		JFrame passwordFrame = new JFrame("Password");
		JPanel passwordPanel = new JPanel();
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

		JPasswordField passwordText = new JPasswordField(13);
		passwordText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		passwordPanel.add(passwordText, cs);
		passwordPanel.setBorder(new LineBorder(Color.GRAY));

		JButton addButton = new JButton("Add");
		JButton cancelButton = new JButton("Cancel");
		addButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					if(passwordText.getPassword().length == 0){
						JOptionPane.showMessageDialog(passwordFrame, "Please enter a password.", "oops ...", JOptionPane.WARNING_MESSAGE);
					} else{
						emailServer.setPassword(new String(passwordText.getPassword()));
						boolean connectSuccessful =  emailServer.connect();
						if(connectSuccessful){
							GUIController.userEmails.add(emailServer.getUsername());
							setEmailJTreeLeftPanel(GUIController.userEmails, getMouseListener());
							passwordFrame.dispose();
						} else{
							JOptionPane.showMessageDialog(passwordFrame, "Error adding account, please try again.", "oops ...", JOptionPane.ERROR_MESSAGE);
							passwordFrame.dispose();
						}
					}
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				passwordFrame.dispose();
			}

		});
		addButton.setFont(new Font("Serif", Font.PLAIN, 23));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 23));

		JPanel buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(addButton);
		buttonPanel.add(cancelButton);

		passwordFrame.getRootPane().setDefaultButton(addButton);
		passwordFrame.add(passwordPanel, BorderLayout.CENTER);
		passwordFrame.add(buttonPanel, BorderLayout.PAGE_END);
		passwordFrame.pack(); //let layout managers in charge of the frame size
		ImageIcon img = new ImageIcon(imageFileName);
		passwordFrame.setIconImage(img.getImage());
		passwordFrame.setLocationRelativeTo(this);
		passwordFrame.setResizable(false);
		passwordFrame.setVisible(true);
	}

	public void setRemoveAccountDialog(SecureMailService emailServer){
		int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this account?", "Caution", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if(dialogResult == JOptionPane.YES_OPTION){
			GUIController.userEmails.remove(emailServer.getUsername());
			GUIController.userEmailObjects.remove(emailServer);
			setEmailJTreeLeftPanel(GUIController.userEmails, getMouseListener());

			Properties prop = new Properties();
			OutputStream output = null;
			try {
				prop.load(new FileInputStream("userconfig.properties"));
				output = new FileOutputStream("userconfig.properties");
				prop.remove("email" + (emailServer.getEmailID() + 1));
				GUIController.counter--;
				prop.store(output, null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}

	public void setLoginFrame(SecureMailService emailServer){//ask for email on emailPanel

		emailServer.setLoginFrame(new JFrame("Log In"));
		JFrame loginFrame = emailServer.getLoginFrame();
		JPanel passwordPanel = new JPanel();
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

		JPasswordField passwordText = new JPasswordField(13);
		passwordText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		passwordPanel.add(passwordText, cs);

		passwordPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		JButton loginButton = new JButton("Log-in");
		JButton cancelButton = new JButton("Cancel");
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				emailServer.setPassword(new String(passwordText.getPassword()));
				System.out.println(emailServer.getUsername());
				System.out.println(new String(passwordText.getPassword()));
				System.out.println("Email server name: " + emailServer.getUsername());
				HttpClient yubikeyClient = HttpClients.createDefault();
				//https://boiling-fjord-84786.herokuapp.com/yubikey
				HttpPost yubikeyPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/yubikey");
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				parameters.add(new BasicNameValuePair("Email", emailServer.getUsername()));
				boolean hasYubikey = false;


				try{
					yubikeyPost.setEntity(new UrlEncodedFormEntity(parameters));//email and yubikey POST as the body of request
					HttpResponse response1 = yubikeyClient.execute(yubikeyPost);//wait for a response from the server
					BufferedReader rd1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));//reader for the response
					String hasYubikeyString = rd1.readLine();//read the response
					if(hasYubikeyString != null && hasYubikeyString.charAt(0) == '1'){
						hasYubikey = true;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				boolean emailAuthenticated2 = false;
				try {
					emailAuthenticated2 = emailServer.connect();//try to connect
					System.out.println("Has yubikey: " + hasYubikey);
					if(!emailAuthenticated2)
						JOptionPane.showMessageDialog(loginFrame, "Wrong password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
					else{
						if(hasYubikey){
							loginFrame.dispose();
							setYubikeyFrame(emailServer);//set up yubikey panel
						} else {//if this email doesnt have yubikey 
							enableAllMenuItems();
							loginFrame.dispose();
						}
					}
				} catch (GeneralSecurityException e3) {//first check to see if it is a correct email/password combo
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (IllegalArgumentException iae){
					JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			} 

		});
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loginFrame.dispose();
			}

		});
		loginButton.setFont(new Font("Serif", Font.PLAIN, 23));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 23));

		JPanel buttonPanel = new JPanel();//create a panel for the buttons
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

	public void setYubikeyFrame(SecureMailService emailServer){//ask for yubikey on yubikeyPanel

		emailServer.setYubikeyFrame(new JFrame("Yubikey"));
		JFrame yubikeyFrame = emailServer.getYubikeyFrame();
		JPanel yubikeyPanel = new JPanel();
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

		JPasswordField yubikeyText = new JPasswordField(13);
		yubikeyText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		yubikeyPanel.add(yubikeyText, cs);

		yubikeyPanel.setBorder(new LineBorder(Color.GRAY));

		JButton verifyButton = new JButton("Verify");
		JButton cancelButton = new JButton("Cancel");
		verifyButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				boolean yubikeyAuthenticated = false, otpAuthenticated = false;
				String verificationString = "";
				HttpClient authenticationClient = HttpClients.createDefault();
				//https://boiling-fjord-84786.herokuapp.com/authenticate
				HttpPost authenticationPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/authenticate");
				int counter = 0;
				while(verificationString == ""){
					try {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(emailServer.getUsername(), new String(yubikeyText.getPassword())));
						//System.out.println(myGui.getEmail());
						authenticationPost.setEntity(new UrlEncodedFormEntity(params));//email and yubikey POST as the body of request
						HttpResponse response2 = authenticationClient.execute(authenticationPost);//wait for a response from the server
						BufferedReader rd2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));//read the response
						verificationString = rd2.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					counter++;
					if(counter == 5){//if verification is not successful, quit the program
						JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Error encountered when verifying, please restart program.", "Error", JOptionPane.ERROR_MESSAGE);
						yubikeyFrame.dispose();
						System.exit(0);
						break;
					}
				}
				if(verificationString.charAt(0) == '1'){//check if email and yubikey binding is correct
					yubikeyAuthenticated = true;
				} else{
					JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Not a valid YubiKey.", "Error", JOptionPane.ERROR_MESSAGE);
				}

				if(yubikeyAuthenticated && verificationString.charAt(1) == '1'){//check if binding is correct and correct OTP
					otpAuthenticated = true;
					JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Successfully verified OTP(One-Time-Password)", "Succeed", JOptionPane.INFORMATION_MESSAGE);
				} else{
					JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Failed to verify OTP(One-Time-Password)", "Failed", JOptionPane.ERROR_MESSAGE);
				}
				System.out.println(verificationString);
				System.out.println(yubikeyAuthenticated);
				System.out.println(otpAuthenticated);
				if(yubikeyAuthenticated && otpAuthenticated){//if everything is correct, then show messages and allow log in
					emailServer.getYubikeyFrame().dispose();
					//myGui.setMainPanel(userEmails);
				} 
			}

		});
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				yubikeyFrame.dispose();
			}

		});
		verifyButton.setFont(new Font("Serif", Font.PLAIN, 23));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 23));

		JPanel buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(verifyButton);
		buttonPanel.add(cancelButton);

		yubikeyFrame.getRootPane().setDefaultButton(verifyButton);
		yubikeyFrame.add(yubikeyPanel, BorderLayout.CENTER);
		yubikeyFrame.add(buttonPanel, BorderLayout.PAGE_END);
		yubikeyFrame.pack(); //let layout managers in charge of the frame size
		ImageIcon img = new ImageIcon(imageFileName);
		yubikeyFrame.setIconImage(img.getImage());
		yubikeyFrame.setResizable(false);
		yubikeyFrame.setVisible(true);
		yubikeyFrame.setLocationRelativeTo(this);
	}

	public void setWriteFrame(Vector<String> userEmails, SecureMailService emailServer){//write email

		//JComboBox<String> emailList = new JComboBox<>(userEmails);
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

		JTextField senderTextField = new JTextField(13);
		senderTextField.setText(emailServer.getUsername());
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

		JTextField recipientTextField = new JTextField(13);
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

		JTextField subjectTextField = new JTextField(13);
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

		JTextArea emailContentText = new JTextArea("Enter your email body ...", 15, 23);//email content
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

		JButton sendButton = new JButton("Send");
		JButton discardButton = new JButton("Discard");
		sendButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				emailServer.setUsername(senderTextField.getText().replaceAll("\\s+", ""));
				System.out.println("Email server is null " + emailServer == null);
				if(recipientTextField != null && !recipientTextField.getText().isEmpty()){
					emailServer.setRecipient(recipientTextField.getText());
				} else{
					JOptionPane.showMessageDialog(writeFrame, "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
					System.out.println("Write Frame null: " + writeFrame == null);
				}
				emailServer.setSubject(subjectTextField.getText());
				try {
					emailServer.send(emailServer.getSMTPServer(), emailContentText.getText());
					//myGui.setSendDebugTextArea();
					JOptionPane.showMessageDialog(writeFrame, "Message sent!");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		discardButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				writeFrame.dispose();
			}

		});
		sendButton.setFont(new Font("Serif", Font.PLAIN, 23));
		discardButton.setFont(new Font("Serif", Font.PLAIN, 23));
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

	public void setSecureWritePanel(Vector<String> userEmails, SecureMailService emailServer){

		//JComboBox<String> emailList = new JComboBox<>(userEmails);
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

		JTextField secureSenderTextField = new JTextField(13);
		secureSenderTextField.setText(emailServer.getUsername());
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

		JTextArea secureEmailContentText = new JTextArea("Enter your email body ...", 15, 23);//email content
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
		JButton discardButton = new JButton("Discard");
		secureSendButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				emailServer.setUsername(secureSenderTextField.getText().replaceAll("\\s+", ""));
				System.out.println("Email server is null " + emailServer == null);
				if(secureRecipientTextField != null && !secureRecipientTextField.getText().isEmpty()){
					emailServer.setRecipient(secureRecipientTextField.getText());
				} else{
					JOptionPane.showMessageDialog(secureWriteFrame, "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
				emailServer.setSubject(secureSubjectTextField.getText());
				try {
					BufferedWriter bw;
					bw = new BufferedWriter(new FileWriter("plain-text.txt"));
					secureEmailContentText.write(bw);
					//myGui.setEmailBodyTextArea();

					emailServer.encryptedSend();
					//myGui.setSendDebugTextArea();
					JOptionPane.showMessageDialog(secureWriteFrame, "Message sent!");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		discardButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				secureWriteFrame.dispose();
			}
		});
		secureSendButton.setFont(new Font("Serif", Font.PLAIN, 23));
		discardButton.setFont(new Font("Serif", Font.PLAIN, 23));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(secureSendButton);
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
		secureWriteFrame.add(mainPanel);
		secureWriteFrame.setResizable(false);
		secureWriteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		secureWriteFrame.pack();
		secureWriteFrame.repaint();
		secureWriteFrame.setLocationRelativeTo(this);
		secureWriteFrame.setVisible(true);
	}

	public void setPopupItems(ActionListener a){
		JMenuItem loginItem = new JMenuItem("Log in");
		loginItem.addActionListener(a);
		JMenuItem getMessageItem = new JMenuItem("Get new messages");
		getMessageItem.addActionListener(a);
		JMenuItem deleteAccountItem = new JMenuItem("Remove account");
		deleteAccountItem.addActionListener(a);
		//getMessageItem.setActionCommand("Get New Messages");
		emailPopupMenu.add(loginItem);
		emailPopupMenu.add(getMessageItem);
		emailPopupMenu.add(deleteAccountItem);
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

	public String getRecipient() {
		if(recipientTextField != null){
			return recipientTextField.getText().replaceAll("\\s+", "");
		}
		return null;
	}

	public String getRecipient(JFrame writeFrame) {
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

	public void setActionListener(ActionListener b){
		this.b = b;
	}

	public ActionListener getActionListener(){
		return b;
	}

	public void setMouseListener(MouseListener a){
		this.a = a;
	}

	public MouseListener getMouseListener(){
		return a;
	}
}
