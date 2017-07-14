package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.openpgp.PGPException;

//, TreeSelectionListener
public class GUIController implements ActionListener, MouseListener{

	GUI myGui;
	SecureMailService emailServer = new SecureMailService();
	private static String smtpServer = "";
	private static String portNumber = "";
	private static String imapServer = "";
	private static String email = "";
	private static String host = "";
	private static boolean hasYubikey = false;
	private static int counter = 0;
	private static Vector<String> userEmails = new Vector<String>();
	private static Vector<SecureMailService> userEmailObjects = new Vector<SecureMailService>();
	private final String fileName = "userconfig.properties";
	private InputStream inputStream;
	private static Hashtable<String, SecureMailService> emailObjectMap = new Hashtable<String, SecureMailService>();

	private final static Hashtable<String, String> smtpServers = new Hashtable<String, String>() {/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		{
			put("GMAIL", "smtp.gmail.com");
			put("OUTLOOK", "smtp.live.com");
			put("OFFICE365", "smtp.office365.com");
			put("YAHOO", "smtp.mail.yahoo.com");
			put("AOL", "smtp.aol.com");
			put("HOTMAIL", "smtp-mail.outlook.com");
		}

	};

	private final static Hashtable<String, String> portNumbers = new Hashtable<String, String>() {/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		{
			put("GMAIL", "465");
			put("OUTLOOK", "587");
			put("OFFICE365", "587");
			put("YAHOO", "465");
			put("AOL", "587");
			put("HOTMAIL", "587");
		}
	};

	private final static Hashtable<String, String> imapServers = new Hashtable<String, String>() {/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		{
			put("GMAIL", "imap.gmail.com");
			put("OUTLOOK", "imap-mail.outlook.com");
			put("OFFICE365", "outlook.office365.com");
			put("YAHOO", "imap.mail.yahoo.com");
			put("AOL", "imap.aol.com");
			put("HOTMAIL", "imap-mail.outlook.com");
		}
	};

	public GUIController() throws Exception{
		//myGui = g;
		getNumberOfEmails();
		loadEmailsFromFile();
		myGui = new GUI(this, this, userEmails);
		myGui.setButtonListener(this);
		setUserEmailObjects();
		//setEmailTreeListener(this, userEmails);
		//myGui.setEmailTreeListener(this, userEmails);
		//myGui.tree.addTreeSelectionListener(this);
		/*Properties prop = new Properties();
		OutputStream output = null;
		output = new FileOutputStream(fileName);
		prop.setProperty("email1", "pinkbunnychickenmarsala@gmail.com");
		prop.setProperty("email2", "pinkbunnychickenmarsala@yahoo.com");
		prop.setProperty("email3", "pinkbunnychickenmarsala@aol.com");
		prop.store(output, null);
		 */


	}

	@Override
	public void actionPerformed(ActionEvent e){
		String buttonName = e.getActionCommand();

		switch(buttonName){

		case "Next":
			email = myGui.getEmail();//get the email
			if(email != null && !email.equals("")){
				host = email.substring(email.indexOf("@") + 1, email.indexOf(".")).toLowerCase();//see what kind of host the user is using
				smtpServer = smtpServers.get(host.toUpperCase());//check what smtp server it is using for that host
				portNumber = portNumbers.get(host.toUpperCase());//check what port it is using for that host
				imapServer = imapServers.get(host.toUpperCase());
				if(smtpServer != null & portNumber != null){
					emailServer.setSMTPServer(smtpServer);//set smtp server
					emailServer.setPort(Integer.parseInt(portNumber));//set port number
					emailServer.setUsername(email);//set user email
					emailServer.setImapHost(imapServer);
					/*System.out.println(smtpServer);
					System.out.println(portNumber);
					System.out.println(host);
					System.out.println(email);*/
					//used to check if this email has a yubikey attached to it
					HttpClient yubikeyClient = HttpClients.createDefault();
					//https://boiling-fjord-84786.herokuapp.com/yubikey
					HttpPost yubikeyPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/yubikey");
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("Email", email));

					try{
						yubikeyPost.setEntity(new UrlEncodedFormEntity(parameters));//email and yubikey POST as the body of request
						HttpResponse response1 = yubikeyClient.execute(yubikeyPost);//wait for a response from the server
						BufferedReader rd1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));//reader for the response
						String hasYubikeyString = rd1.readLine();//read the response
						System.out.println("Has yubikey String:" + hasYubikeyString);
						if(hasYubikeyString != null){
							if(hasYubikeyString.charAt(0) == '0')//if response is 0, false
								hasYubikey = false;
							else if(hasYubikeyString.charAt(0) == '1')//if response is 1, true
								hasYubikey = true;
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					myGui.emailPanel.setVisible(false);//fisrt hide email panel
					myGui.buttonPanel.setVisible(false);//hide button panel
					myGui.buttonPanel.removeAll();//remove whatever is in button panel
					myGui.addAccountFrame.repaint();//repaint the gui

					myGui.setPasswordPanel();//set up password panel
					myGui.passwordPanel.setVisible(true);//make the password panel visible
					myGui.addAccountFrame.repaint();//repaint the gui

					break;
				} else {
					JOptionPane.showMessageDialog(myGui.addAccountFrame, "Email not supported, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
					break;
				}
			} else {
				JOptionPane.showMessageDialog(myGui.addAccountFrame, "Please enter an email.", "oops ...", JOptionPane.WARNING_MESSAGE);
				break;
			}

		/*case "Sign-in":
			boolean emailAuthenticated = false;
			emailServer.setPassword(myGui.getPassword());
			try {
				emailAuthenticated = emailServer.connect();//try to connect
				System.out.println("Has yubikey: " + hasYubikey);
				if(!emailAuthenticated)
					JOptionPane.showMessageDialog(myGui.addAccountFrame, "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
				else{
					if(hasYubikey){
						myGui.setYubikeyFrame(emailServer);//set up yubikey panel
					} else {//if this email doesnt have yubikey 
						myGui.enableAllMenuItems();
						emailServer.getLoginFrame().dispose();
						//myGui.setMainPanel(userEmails, this);
					}
				}
			} catch (GeneralSecurityException e3) {//first check to see if it is a correct email/password combo
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (IllegalArgumentException iae){
				JOptionPane.showMessageDialog(myGui.addAccountFrame, "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			break;*/

		/*case "Verify":
			boolean yubikeyAuthenticated = false, otpAuthenticated = false;
			String verificationString = "";
			HttpClient authenticationClient = HttpClients.createDefault();
			//https://boiling-fjord-84786.herokuapp.com/authenticate
			HttpPost authenticationPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/authenticate");
			int counter = 0;
			while(verificationString == ""){
				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair(email, myGui.getYubikey()));
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
					myGui.dispose();
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
				emailServer.getLoginFrame().dispose();
				myGui.enableAllMenuItems();
				//myGui.setMainPanel(userEmails);
			} 
			break;*/

		/*case "Cancel":
			emailServer.getLoginFrame().dispose();
			break;*/

			/*case "Send":
			emailServer = emailObjectMap.get(myGui.getEmailFromCombobox());
			emailServer.setUsername(myGui.getEmailFromCombobox());
			System.out.println("Email server is null " + emailServer == null);
			if(myGui.getPassword() == null || myGui.getPassword().isEmpty()){//if the user has not logged in 
				JOptionPane.showMessageDialog(myGui.addAccountFrame, "Please log in first.", "Failed", JOptionPane.ERROR_MESSAGE);
				myGui.setLoginFrame(emailServer);
				break;
			} else{
				emailServer.setPassword(myGui.getPassword());
			}

			if(myGui.getRecipient() != null && !myGui.getRecipient().isEmpty()){
				emailServer.setRecipient(myGui.getRecipient());
			} else{
				JOptionPane.showMessageDialog(emailServer.getWriteFrame(), "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
				System.out.println("Write Frame null: " + emailServer.getWriteFrame() == null);
				break;
			}
			emailServer.setSubject(myGui.getSubject());
			try {
				emailServer.send(host, myGui.getEmailContentText());
				myGui.setSendDebugTextArea();
				JOptionPane.showMessageDialog(emailServer.getWriteFrame(), "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;*/

		case "Add New Account":
			myGui.setAddAccountFrame();
			break;

		case "Log in":
			//System.out.println(emailServer.getUsername());
			if(emailServer.isSmtpLoggedIn()){
				JOptionPane.showMessageDialog(myGui, "Already logged in", "oops ...", JOptionPane.INFORMATION_MESSAGE);
			} else{
				myGui.setLoginFrame(emailServer);
			}
			break;

			/*case "Log-in":
			//emailServer = emailObjectMap.get(myGui.getEmailFromCombobox());
			emailServer.setPassword(myGui.getPassword());
			System.out.println("Email server name: " + emailServer.getUsername());
			if(email != null && !email.equals("")){
				host = email.substring(email.indexOf("@") + 1, email.indexOf(".")).toLowerCase();//see what kind of host the user is using
				smtpServer = smtpServers.get(host.toUpperCase());//check what smtp server it is using for that host
				portNumber = portNumbers.get(host.toUpperCase());//check what port it is using for that host
				imapServer = imapServers.get(host.toUpperCase());
				if(smtpServer != null & portNumber != null){
					emailServer.setHostName(smtpServer);//set smtp server
					emailServer.setPort(Integer.parseInt(portNumber));//set port number
					emailServer.setUsername(email);//set user email
					emailServer.setImapHost(imapServer);
					System.out.println(smtpServer);
					System.out.println(portNumber);
					System.out.println(host);
					System.out.println(email);
					//used to check if this email has a yubikey attached to it
					HttpClient yubikeyClient = HttpClients.createDefault();
					//https://boiling-fjord-84786.herokuapp.com/yubikey
					HttpPost yubikeyPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/yubikey");
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("Email", email));

					try{
						yubikeyPost.setEntity(new UrlEncodedFormEntity(parameters));//email and yubikey POST as the body of request
						HttpResponse response1 = yubikeyClient.execute(yubikeyPost);//wait for a response from the server
						BufferedReader rd1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));//reader for the response
						String hasYubikeyString = rd1.readLine();//read the response
						//System.out.println(hasYubikeyString);
						if(hasYubikeyString != null){
							if(hasYubikeyString.charAt(0) == '0')//if response is 0, false
								hasYubikey = false;
							else if(hasYubikeyString.charAt(0) == '1')//if response is 1, true
								hasYubikey = true;
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					myGui.passwordPanel.setVisible(false);//fisrt hide email panel
					myGui.buttonPanel.setVisible(false);//hide button panel
					myGui.buttonPanel.removeAll();//remove whatever is in button panel
					emailServer.getLoginFrame().repaint();//repaint the gui

					boolean emailAuthenticated2 = false;
					try {
						emailAuthenticated2 = emailServer.connect(host);//try to connect
						System.out.println("Has yubikey: " + hasYubikey);
						if(!emailAuthenticated2)
							JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
						else{
							if(hasYubikey){
								myGui.setYubikeyPanel(emailServer);//set up yubikey panel
							} else {//if this email doesnt have yubikey 
								myGui.enableAllMenuItems();
								emailServer.getLoginFrame().dispose();
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
					break;
				} else {
					JOptionPane.showMessageDialog(emailServer.getLoginFrame(), "Email not supported, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
					break;
				}
			} 
			break;
			 */
		/*case "Secure Send":
			BufferedWriter bw;
			emailServer = emailObjectMap.get(myGui.getEmailFromCombobox());
			emailServer.setUsername(myGui.getEmailFromCombobox());
			if(myGui.getPassword() == null || myGui.getPassword().isEmpty()){//if the user has not logged in 
				JOptionPane.showMessageDialog(myGui.addAccountFrame, "Please log in first.", "Failed", JOptionPane.ERROR_MESSAGE);
				myGui.setLoginFrame(emailServer);
				break;
			} else{
				emailServer.setPassword(myGui.getPassword());
			}

			if(myGui.getSecureRecipient() != null && !myGui.getSecureRecipient().isEmpty()){
				emailServer.setRecipient(myGui.getSecureRecipient());
			} else{
				JOptionPane.showMessageDialog(myGui.addAccountFrame, "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
				break;
			}

			try {
				bw = new BufferedWriter(new FileWriter("plain-text.txt"));
				myGui.secureEmailContentText.write(bw);
				//myGui.setEmailBodyTextArea();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				emailServer.encryptedSend();
				myGui.setSecureSendDebugTextArea();;
				JOptionPane.showMessageDialog(emailServer.getSecureWriteFrame(), "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;*/

		case "Get new messages":
			try {
				emailServer.receiveEmail();
				BufferedReader br = new BufferedReader(new FileReader("dec-plain-text.txt"));
				String message = "";

				String line = null;
				while ((line = br.readLine()) != null) {
					message = message + line + "\n";
				}
				br.close();
				//myGui.textAreaPanel.setVisible(false);
				myGui.repaint();
				//myGui.setReceivedEmailTextArea(message);
				myGui.repaint();
				JOptionPane.showMessageDialog(myGui, "Message received!");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			break;

		case "Generate Key Pair":
			TestBCOpenPGP x = new TestBCOpenPGP();
			try {
				x.genKeyPair();
			} catch (InvalidKeyException | NoSuchProviderException | SignatureException | NoSuchAlgorithmException
					| IOException | PGPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "Exit":
			System.exit(0);
			break;	

		default:
			break;
		}
	}

	public void getNumberOfEmails() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));//just to see how many records there are
		String line = br.readLine();
		line = br.readLine();
		while(line != null){
			counter++;
			line = br.readLine();
		}
		br.close();
		//System.out.println(counter);
	}

	public void loadEmailsFromFile() throws IOException{
		Properties prop = new Properties();//get each of the email and store them in the userEmail vector
		inputStream = new FileInputStream(fileName);
		prop.load(inputStream);
		String user;
		if(inputStream != null){
			for(int i = 1; i <= counter; i++){
				user = prop.getProperty("email" + i);
				//System.out.println(user);
				userEmails.add(user);
			}
		}
		//return userEmails;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		JTree tree = (JTree) e.getComponent();
		tree.getLastSelectedPathComponent();
		int row = tree.getRowForLocation(e.getX(), e.getY());
		//System.out.println(row);
		//2 is the number of clicks, row != 0 means if it is not root, get the path of which the item is clicked
		if(tree.getLastSelectedPathComponent() != null){
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			if(e.getClickCount() == 2 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Secure write"){
				emailServer = emailObjectMap.get(node.getRoot().toString());
				if(emailServer.isSmtpLoggedIn()){
					myGui.setSecureWritePanel(userEmails, emailServer);
				}
				//System.out.println("double clicked");
			} else if(e.getClickCount() == 2 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Write"){
				emailServer = emailObjectMap.get(node.getRoot().toString());
				if(emailServer.isSmtpLoggedIn()){
					myGui.setWriteFrame(userEmails, emailServer);
				}
				//System.out.println("double clicked");
			} else if(e.getClickCount() == 1 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Inbox"){
				try {
					System.out.println("Root:" + node.getRoot().toString());
					emailServer = emailObjectMap.get(node.getRoot().toString());
					System.out.println("email server is null" + emailServer == null);
					if(emailServer.isSmtpLoggedIn()){
						myGui.setDisplayPanel(emailServer);
						//System.out.println(emailServer.getEmailTable() == null);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if(SwingUtilities.isRightMouseButton(e)){

				if(node.isRoot()) {
					emailServer = emailObjectMap.get(node.getRoot().toString());
					myGui.emailPopupMenu.show(tree, e.getX(), e.getY());
					email = node.getUserObject().toString();
					//myGui.emailTextField = new JTextField(email);
					//emailServer = emailObjectMap.get(email);
					emailServer.setUsername(email);
				}
			} else {
				/*if(tree.getLastSelectedPathComponent() != null && !emailServer.isSmtpLoggedIn()){
					JOptionPane.showMessageDialog(myGui, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
				}*/
				//tree.getSelectionModel().clearSelection();
			} 
			if(row == -1) //When user clicks on the "empty surface"
				tree.getSelectionModel().clearSelection();
			//tree.clearSelection();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	public void setEmailTreeListener(MouseListener a, Vector<String> userEmails){//might not needed at all
		for(int i = 0; i < myGui.trees.size(); i++){
			JTree treeRoot = myGui.trees.elementAt(i);
			treeRoot.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			treeRoot.addTreeSelectionListener(new TreeSelectionListener() {//add listener to individual tree
				public void valueChanged(TreeSelectionEvent e) {
					//Returns the last path element of the selection.
					//This method is useful only when the selection model allows a single selection.
					DefaultMutableTreeNode node;
					node = (DefaultMutableTreeNode)
							treeRoot.getLastSelectedPathComponent();
					if(node.isLeaf() && node.getUserObject().toString() == "Inbox"){
						System.out.println("leaf"); 
						try {
							/*if(emailServer.getEmailTable() == null){
								myGui.setDisplayPanel(emailServer, emailServer.getEmailTable());
							}*/
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//setSecureWritePanel(userEmails);
						return;
					}

				}});
			treeRoot.addMouseListener(a);
		}
	}

	public void setUserEmailObjects(){
		for(int i = 0; i < userEmails.size(); i++){
			SecureMailService emailServer = new SecureMailService();
			emailServer.setUsername(userEmails.elementAt(i));
			userEmailObjects.add(emailServer);

			String emailType = (userEmails.elementAt(i).substring(emailServer.getUsername().indexOf("@") + 1, emailServer.getUsername().indexOf("."))).toUpperCase();
			emailServer.setSMTPServer(smtpServers.get(emailType));
			emailServer.setEmailType(emailType);
			emailServer.setPort(Integer.parseInt(portNumbers.get(emailType)));
			emailServer.setImapHost(imapServers.get(emailType));

			System.out.println(userEmails.elementAt(i));
			emailObjectMap.put(userEmails.elementAt(i), emailServer);
		};
	}
}
