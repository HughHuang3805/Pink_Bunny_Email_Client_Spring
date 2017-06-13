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
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


@SuppressWarnings("deprecation")
public class GUIController implements ActionListener{

	GUI myGui;
	SSLMailServer mailServer = new SSLMailServer();

	public GUIController(GUI g) throws Exception{//constructor for no command line arguments
		myGui = g;
		myGui.setButtonListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String buttonName = e.getActionCommand();

		switch(buttonName){

		case "Sign-in":
			boolean emailAuthenticated = false, yubikeyAuthenticated = false, otpAuthenticated = false;
			String verificationString = "";
			mailServer.setSMTP_AUTH_USER(myGui.getEmail());
			mailServer.setSMTP_AUTH_PWD(myGui.getPassword());
			mailServer.setMyGui(myGui);
			try {
				emailAuthenticated = mailServer.connect();
			} catch (GeneralSecurityException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}//first check to see if it is a correct email/password combo
			
			try {//otp verification
				if(emailAuthenticated){//if the password and username are correct
					@SuppressWarnings("resource")
					HttpClient client = new DefaultHttpClient();
					//https://boiling-fjord-84786.herokuapp.com/authenticate
					HttpPost post = new HttpPost("https://boiling-fjord-84786.herokuapp.com/authenticate");
					int counter = 0;
					//this line is used to process verificationString
					while(verificationString == ""){
						try {
							List<NameValuePair> params = new ArrayList<NameValuePair>();
						    params.add(new BasicNameValuePair(myGui.getEmail(), myGui.getYubikey()));
							post.setEntity(new UrlEncodedFormEntity(params));//email and yubikey POST as the body of request
							HttpResponse response = client.execute(post);//wait for a response from the server
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));//read the response
							verificationString = rd.readLine();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						counter++;
						System.out.println(verificationString);
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
				}
			} catch (IllegalArgumentException iae){
				JOptionPane.showMessageDialog(myGui, "Not a valid OTP(One-Time-Password) format.", "Error", JOptionPane.ERROR_MESSAGE);
			}

			if(emailAuthenticated && otpAuthenticated && yubikeyAuthenticated){//if everything is correct, then show messages and allow log in
				myGui.loginPanel.setVisible(false);
				myGui.setEmailBodyTextArea();
				myGui.menu1.setEnabled(true);
				myGui.panel = new JPanel();
				myGui.setResizable(true);
				myGui.signInButton.setText("Send");
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
				mailServer.send();
				myGui.setSendDebugTextArea();
				JOptionPane.showMessageDialog(myGui, "Message sent!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "Receive":
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
