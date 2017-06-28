package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.openpgp.PGPException;


public class GUIController implements ActionListener{

	GUI myGui;
	SecureMailService mailServer = new SecureMailService();
	ReceiveEmail receiveEmail = new ReceiveEmail();
	private static String smtpServer = "";
	private static String portNumber = "";
	private static String email = "";
	private static String host = "";
	private static boolean hasYubikey = false;

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

	public GUIController(GUI g) throws Exception{
		myGui = g;
		myGui.setButtonListener(this);
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
						myGui.setWelcomeScreen();
					}
				}
			} catch (GeneralSecurityException e3) {//first check to see if it is a correct email/password combo
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (IllegalArgumentException iae){
				JOptionPane.showMessageDialog(myGui.loginFrame, "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
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
					params.add(new BasicNameValuePair(myGui.getEmail(), myGui.getYubikey()));
					System.out.println(myGui.getEmail());
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
				myGui.enableAllMenuItems();
				myGui.setWelcomeScreen();
			} 
			break;

		case "Cancel":
			myGui.loginFrame.dispose();
			break;

		case "Send":
			mailServer.setRecipient(myGui.getRecipient());
			mailServer.setSubject(myGui.getSubject());

			receiveEmail.setUsername(myGui.getEmail());
			receiveEmail.setPassword(myGui.getPassword());

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
			myGui.setEmailPanel();
			break;

		case "Secure Send":
			BufferedWriter bw;
			mailServer.setRecipient(myGui.getSecureRecipient());
			mailServer.setSubject(myGui.getSecureSubject());

			receiveEmail.setUsername(myGui.getEmail());
			receiveEmail.setPassword(myGui.getPassword());

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

		case "Get New Messages":
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
			myGui.setWritePanel();
			break;

		case "Secure Write":
			myGui.setSecureWritePanel();
			break;

		case "Exit":
			System.exit(0);
			break;	

		default:
			break;
		}
	}
}
