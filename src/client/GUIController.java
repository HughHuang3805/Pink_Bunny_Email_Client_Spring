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
	SecureMailService mailServer = new SecureMailService();
	ReceiveEmail receiveEmail = new ReceiveEmail();
	private static String smtpServer = "";
	private static String portNumber = "";
	private static String email = "";
	private static String host = "";
	private static boolean hasYubikey = false;
	private static int counter = 0;
	private static Vector<String> userEmails = new Vector<String>();
	private final String fileName = "userconfig.properties";
	private InputStream inputStream;

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

	public GUIController() throws Exception{
		//myGui = g;
		getNumberOfEmails();
		getConfigEmails();
		myGui = new GUI(this, this, receiveEmail, userEmails);
		myGui.setButtonListener(this);
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
				if(smtpServer != null & portNumber != null){
					mailServer.setHostName(smtpServer);//set smtp server
					mailServer.setPort(Integer.parseInt(portNumber));//set port number
					mailServer.setUser(email);//set user email
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
						System.out.println(hasYubikeyString);
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
					myGui.loginFrame.repaint();//repaint the gui

					myGui.setPasswordPanel();//set up password panel
					myGui.passwordPanel.setVisible(true);//make the password panel visible
					myGui.loginFrame.repaint();//repaint the gui

					break;
				} else {
					JOptionPane.showMessageDialog(myGui.loginFrame, "Email not supported, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
					break;
				}
			} else {
				JOptionPane.showMessageDialog(myGui.loginFrame, "Please enter an email.", "oops ...", JOptionPane.WARNING_MESSAGE);
				break;
			}

		case "Sign-in":
			boolean emailAuthenticated = false;
			mailServer.setPW(myGui.getPassword());
			try {
				emailAuthenticated = mailServer.connect(host);//try to connect
				System.out.println("Has yubikey: " + hasYubikey);
				if(!emailAuthenticated)
					JOptionPane.showMessageDialog(myGui.loginFrame, "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
				else{
					if(hasYubikey){
						myGui.setYubikeyPanel();//set up yubikey panel
					} else {//if this email doesnt have yubikey 
						myGui.enableAllMenuItems();
						myGui.loginFrame.dispose();
						myGui.setMainPanel(userEmails, receiveEmail, this);
					}
				}
			} catch (GeneralSecurityException e3) {//first check to see if it is a correct email/password combo
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (IllegalArgumentException iae){
				JOptionPane.showMessageDialog(myGui.loginFrame, "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			break;

		case "Verify":
			boolean yubikeyAuthenticated = false, otpAuthenticated = false;
			String verificationString = "";
			HttpClient authenticationClient = HttpClients.createDefault();
			//https://boiling-fjord-84786.herokuapp.com/authenticate
			HttpPost authenticationPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/authenticate");
			int counter = 0;
			while(verificationString == ""){
				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair(myGui.getEmailFromList(), myGui.getYubikey()));
					System.out.println(myGui.getEmailFromList());
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
					JOptionPane.showMessageDialog(myGui.loginFrame, "Error encountered when verifying, please restart program.", "Error", JOptionPane.ERROR_MESSAGE);
					myGui.dispose();
					System.exit(0);
					break;
				}
			}
			if(verificationString.charAt(0) == '1'){//check if email and yubikey binding is correct
				yubikeyAuthenticated = true;
			} else{
				JOptionPane.showMessageDialog(myGui.loginFrame, "Not a valid YubiKey.", "Error", JOptionPane.ERROR_MESSAGE);
			}

			if(yubikeyAuthenticated && verificationString.charAt(1) == '1'){//check if binding is correct and correct OTP
				otpAuthenticated = true;
				JOptionPane.showMessageDialog(myGui.loginFrame, "Successfully verified OTP(One-Time-Password)", "Succeed", JOptionPane.INFORMATION_MESSAGE);
			} else{
				JOptionPane.showMessageDialog(myGui.loginFrame, "Failed to verify OTP(One-Time-Password)", "Failed", JOptionPane.ERROR_MESSAGE);
			}
			System.out.println(verificationString);
			System.out.println(yubikeyAuthenticated);
			System.out.println(otpAuthenticated);
			if(yubikeyAuthenticated && otpAuthenticated){//if everything is correct, then show messages and allow log in
				myGui.loginFrame.dispose();
				myGui.enableAllMenuItems();
				//myGui.setMainPanel(userEmails);
			} 
			break;

		case "Cancel":
			myGui.loginFrame.dispose();
			break;

		case "Send":
			receiveEmail.setUsername(myGui.getEmailFromList());
			if(myGui.getPassword() == null || myGui.getPassword().isEmpty()){//if the user has not logged in 
				JOptionPane.showMessageDialog(myGui.loginFrame, "Please log in first.", "Failed", JOptionPane.ERROR_MESSAGE);
				myGui.setLoginFrame();
				break;
			} else{
				receiveEmail.setPassword(myGui.getPassword());
			}
			
			if(myGui.getRecipient() != null && !myGui.getRecipient().isEmpty()){
				mailServer.setRecipient(myGui.getRecipient());
			} else{
				JOptionPane.showMessageDialog(myGui.loginFrame, "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
				break;
			}
			mailServer.setSubject(myGui.getSubject());
			try {
				mailServer.send(host, myGui.getEmailContentText());
				myGui.setSendDebugTextArea();
				JOptionPane.showMessageDialog(myGui.writeFrame, "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "Add New Account":
			myGui.setLoginFrame();;
			break;

		case "Secure Send":
			BufferedWriter bw;
			receiveEmail.setUsername(myGui.getEmailFromList());
			if(myGui.getPassword() == null || myGui.getPassword().isEmpty()){//if the user has not logged in 
				JOptionPane.showMessageDialog(myGui.loginFrame, "Please log in first.", "Failed", JOptionPane.ERROR_MESSAGE);
				myGui.setLoginFrame();
				break;
			} else{
				receiveEmail.setPassword(myGui.getPassword());
			}
			
			if(myGui.getSecureRecipient() != null && !myGui.getSecureRecipient().isEmpty()){
				mailServer.setRecipient(myGui.getSecureRecipient());
			} else{
				JOptionPane.showMessageDialog(myGui.loginFrame, "Please specify recipient.", "Failed", JOptionPane.ERROR_MESSAGE);
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
				mailServer.encryptedSend(host);
				myGui.setSecureSendDebugTextArea();;
				JOptionPane.showMessageDialog(myGui.secureWriteFrame, "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "Discard":
			myGui.writeFrame.dispose();
			break;

		case "Secure Discard":
			myGui.secureWriteFrame.dispose();
			break;

		case "Get new messages":
			try {
				receiveEmail.receiveEmail();
				BufferedReader br = new BufferedReader(new FileReader("dec-plain-text.txt"));
				String message = "";

				String line = null;
				while ((line = br.readLine()) != null) {
					message = message + line + "\n";
				}
				br.close();
				myGui.textAreaPanel.setVisible(false);
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

		case "Write":
			myGui.setWritePanel(userEmails);
			break;

		case "Secure Write":
			myGui.setSecureWritePanel(userEmails);
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
		System.out.println(counter);
	}

	public void getConfigEmails() throws IOException{
		Properties prop = new Properties();//get each of the email and store them in the userEmail vector
		inputStream = new FileInputStream(fileName);
		prop.load(inputStream);
		String user;
		if(inputStream != null){
			for(int i = 1; i <= counter; i++){
				user = prop.getProperty("email" + i);
				System.out.println(user);
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
		//2 is the number of clicks, row != 0 means if it is not root, get the path of which the item is clicked
		if(tree.getLastSelectedPathComponent() != null){
			if(e.getClickCount() == 2 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Secure write"){
				myGui.setSecureWritePanel(userEmails);
				System.out.println("double clicked");
			} else if(e.getClickCount() == 2 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Write"){
				myGui.setWritePanel(userEmails);
				System.out.println("double clicked");
			} else if(e.getClickCount() == 1 && row != 0 && tree.getLastSelectedPathComponent().toString() == "Inbox"){
				try {
					if(myGui.emailTable == null){
						myGui.setDisplayPanel(receiveEmail);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(SwingUtilities.isRightMouseButton(e)){
				DefaultMutableTreeNode node;
				node = (DefaultMutableTreeNode)
						tree.getLastSelectedPathComponent();
				//System.out.println(tree.getX() + " " + tree.getY());
				//System.out.println(e.getX() + " " + e.getY());
				if(node.isRoot())
					myGui.emailPopupMenu.show(tree, e.getX(), e.getY());
			}
			if(row==-1) //When user clicks on the "empty surface"
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
					/*if (node == null){
						//Nothing is selected.     
						System.out.println("nothing is selected");
						return;
					}*/
					/*if (node.isRoot() ) {
						System.out.println("root");
						//treeRoot.clearSelection();
						return;
					}*/if(node.isLeaf() && node.getUserObject().toString() == "Inbox"){
						System.out.println("leaf"); 
						try {
							myGui.setDisplayPanel(receiveEmail);
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
}
