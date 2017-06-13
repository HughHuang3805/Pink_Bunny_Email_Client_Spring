package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import org.bouncycastle.openpgp.PGPException;


public class SSLMailServer {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final int SMTP_HOST_PORT = 465;
	private String SMTP_AUTH_USER;
	private String SMTP_AUTH_PWD;
	private GUI myGui;

	public static void main(String[] args) throws Exception{
		//new SimpleSSLMail().send();
	}

	public void send() throws Exception{
		TestBCOpenPGP x = new TestBCOpenPGP();
		x.encrypt();

		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", SMTP_HOST_NAME);
		props.put("mail.smtps.auth", "true");
		//props.put("mail.smtps.ssl.trust", "*");
		//props.put("mail.smtps.quitwait", "false");

		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(true);
		PrintStream ps = new PrintStream(new File("debug.txt"));//output to debug.txt
		mailSession.setDebugOut(ps);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject("Testing SMTP-SSL");

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		String filename = "cypher-text.dat";
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		MimeMultipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);

		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress("PinkBunnyChickenMarsala2@gmail.com"));

		System.out.println(SMTP_AUTH_USER);
		System.out.println(SMTP_AUTH_PWD);
		transport.connect
		(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	public boolean connect() throws GeneralSecurityException{
		Properties props = new Properties();
		
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", SMTP_HOST_NAME);
		props.put("mail.smtps.auth", "true");
		//props.put("mail.smtps.ssl.trust", "*");
		//props.put("mail.smtps.quitwait", "false");
		
		Session mailSession = Session.getDefaultInstance(props);
		Transport transport;
		try {
			transport = mailSession.getTransport();
			transport.connect
			(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
		} catch (javax.mail.NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			JOptionPane.showMessageDialog(myGui, "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
			return false;
		} catch(Exception e){
			JOptionPane.showMessageDialog(myGui, "Wrong email or password, try again.", "oops ...", JOptionPane.WARNING_MESSAGE);
			//e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getEncryptedString() throws NoSuchProviderException, IOException, PGPException{
		String encryptedString;
		TestBCOpenPGP x = new TestBCOpenPGP();
		x.encrypt();
		FileInputStream file = new FileInputStream("cypher-text.dat");
		try( BufferedReader br =
				new BufferedReader( new InputStreamReader(file)))
				{
			StringBuilder sb = new StringBuilder();
			while(( encryptedString = br.readLine()) != null ) {
				sb.append( encryptedString );
				sb.append( '\n' );
			}
			return sb.toString();
				}
	}

	public String getSMTP_AUTH_USER() {
		return SMTP_AUTH_USER;
	}

	public void setSMTP_AUTH_USER(String sMTP_AUTH_USER) {
		SMTP_AUTH_USER = sMTP_AUTH_USER;
	}

	public String getSMTP_AUTH_PWD() {
		return SMTP_AUTH_PWD;
	}

	public void setSMTP_AUTH_PWD(String sMTP_AUTH_PWD) {
		SMTP_AUTH_PWD = sMTP_AUTH_PWD;
	}

	public GUI getMyGui() {
		return myGui;
	}

	public void setMyGui(GUI myGui) {
		this.myGui = myGui;
	}

}