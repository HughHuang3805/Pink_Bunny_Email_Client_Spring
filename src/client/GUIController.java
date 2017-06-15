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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


public class GUIController implements ActionListener{

	GUI myGui;
	SSLMailServer mailServer = new SSLMailServer();
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
	}};

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
	}};

	public GUIController(GUI g) throws Exception{
		myGui = g;
		myGui.setButtonListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String buttonName = e.getActionCommand();
		
		switch(buttonName){

		case "Next":
			email = myGui.getEmail();
			host = email.substring(email.indexOf("@") + 1, email.indexOf("."));
			smtpServer = smtpServers.get(host.toUpperCase());
			portNumber = portNumbers.get(host.toUpperCase());
			mailServer.setSMTP_HOST_NAME(smtpServer);
			mailServer.setSMTP_HOST_PORT(Integer.parseInt(portNumber));
			mailServer.setSMTP_AUTH_USER(email);
			System.out.println(smtpServer);
			System.out.println(portNumber);
			System.out.println(host);
			System.out.println(email);
			HttpClient yubikeyClient = HttpClients.createDefault();
			//https://boiling-fjord-84786.herokuapp.com/authenticate
			HttpPost yubikeyPost = new HttpPost("http://localhost:8080/Pink_Bunny_Email_Server_Spring/yubikey");
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("Email", email));
			
			try{
			yubikeyPost.setEntity(new UrlEncodedFormEntity(parameters));//email and yubikey POST as the body of request
			HttpResponse response1 = yubikeyClient.execute(yubikeyPost);//wait for a response from the server
			BufferedReader rd1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));//read the response
			String hasYubikeyString = rd1.readLine();
			if(hasYubikeyString != null){
				if(hasYubikeyString == "0")
					hasYubikey = false;
				else
					hasYubikey = true;
			}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			myGui.emailPanel.setVisible(false);//fisrt hide email panel
			myGui.buttonPanel.setVisible(false);//hide button panel
			myGui.buttonPanel.removeAll();//remove whatever is in button panel
			myGui.repaint();//repaint the gui

			myGui.setPasswordPanel();//set up password panel
			myGui.passwordPanel.setVisible(true);//make the password panel visible
			myGui.repaint();//repaint the gui
			break;

		case "Sign-in":
			boolean emailAuthenticated = false, yubikeyAuthenticated = false, otpAuthenticated = false;
			String verificationString = "";
			//mailServer.setSMTP_AUTH_USER(myGui.getEmail());
			mailServer.setSMTP_AUTH_PWD(myGui.getPassword());
			try {
				emailAuthenticated = mailServer.connect(host);
				if(!emailAuthenticated)
					JOptionPane.showMessageDialog(myGui, "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
			} catch (GeneralSecurityException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}//first check to see if it is a correct email/password combo

			try {//otp verification
				if(emailAuthenticated && hasYubikey){//if the password and username are correct
					HttpClient authenticationClient = HttpClients.createDefault();
					//https://boiling-fjord-84786.herokuapp.com/authenticate
					HttpPost authenticationPost = new HttpPost("https://boiling-fjord-84786.herokuapp.com/authenticate");
					int counter = 0;
					//this line is used to process verificationString
					while(verificationString == ""){
						try {
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair(myGui.getEmail(), myGui.getYubikey()));
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
							JOptionPane.showMessageDialog(myGui, "Error encountered when connecting, please restart program.", "Error", JOptionPane.ERROR_MESSAGE);
							myGui.dispose();
							System.exit(0);
							break;
						}
					}
					if(verificationString.charAt(0) == '1'){//check if email and yubikey binding is correct
						yubikeyAuthenticated = true;
					} else{
						JOptionPane.showMessageDialog(myGui, "Not a valid YubiKey.", "Error", JOptionPane.ERROR_MESSAGE);
					}

					if(yubikeyAuthenticated && verificationString.charAt(1) == '1'){//check if binding is correct and correct OTP
						otpAuthenticated = true;
						JOptionPane.showMessageDialog(myGui, "Successfully verified OTP(One-Time-Password)", "Succeed", JOptionPane.INFORMATION_MESSAGE);
					} else{
						JOptionPane.showMessageDialog(myGui, "Failed to verify OTP(One-Time-Password)", "Failed", JOptionPane.ERROR_MESSAGE);
					}
					if(emailAuthenticated && yubikeyAuthenticated && otpAuthenticated && hasYubikey){//if everything is correct, then show messages and allow log in
						myGui.passwordPanel.setVisible(false);
						myGui.repaint();
						myGui.setEmailBodyTextArea();
						myGui.menu1.setEnabled(true);
						myGui.panel = new JPanel();
						myGui.setResizable(true);
						myGui.signInButton.setText("Send");
					} 
				} else if(emailAuthenticated){
					myGui.passwordPanel.setVisible(false);
					myGui.repaint();
					myGui.setEmailBodyTextArea();
					myGui.menu1.setEnabled(true);
					myGui.panel = new JPanel();
					myGui.setResizable(true);
					myGui.signInButton.setText("Send");
				}
			} catch (IllegalArgumentException iae){
				JOptionPane.showMessageDialog(myGui, "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
			} 

			
			break;

		case "Cancel":
			myGui.dispose();
			break;

		case "Send":
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("plain-text.txt"));
				myGui.x.write(bw);
				//myGui.setEmailBodyTextArea();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				mailServer.send(host);
				myGui.setSendDebugTextArea();
				JOptionPane.showMessageDialog(myGui, "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "Get All New Messages":
			try {
				ReceiveEmail.receiveEmail();
				BufferedReader br = new BufferedReader(new FileReader("dec-plain-text.txt"));
				String message = "";

				String line = null;
				while ((line = br.readLine()) != null) {
					message = message + line + "\n";
				}
				br.close();
				myGui.panel.setVisible(false);
				myGui.repaint();
				myGui.setReceivedEmailTextArea(message);
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

		case "Exit":
			System.exit(0);
			break;	

		default:
			break;

		}
	}
}
