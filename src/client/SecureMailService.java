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
import org.bouncycastle.openpgp.PGPException;


public class SecureMailService {

	private String SMTP_HOST_NAME;
	private int SMTP_HOST_PORT;
	private String SMTP_AUTH_USER;
	private String SMTP_AUTH_PWD;
	private String SMTP_RECIPIENT;
	private String subject;
	
	public void send(String host) throws Exception{
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
		message.setSubject(getSubject());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		String filename = "cypher-text.dat";
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		MimeMultipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);

		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(getRecipient()));

		transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	public boolean connect(String host) throws GeneralSecurityException{
		Properties props = new Properties();
		
		switch(host){

		case "gmail":
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			//props.put("mail.smtps.ssl.trust", "*");
			//props.put("mail.smtps.quitwait", "false");
			break;

		case "hotmail":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;
			
		case "outlook":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;
			
		case "office365":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;
			
		case "aol":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;
			
		case "yahoo":
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			//props.put("mail.smtps.ssl.trust", "*");
			//props.put("mail.smtps.quitwait", "false");
			break;
			
		default:
			return false;
		}
		System.out.println(host);
		Session mailSession = Session.getDefaultInstance(props);
		Transport transport;
		try {
			transport = mailSession.getTransport();
			transport.connect
			(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
		} catch (javax.mail.NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch(Exception e){
			e.printStackTrace();
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

	public String getUser() {
		return SMTP_AUTH_USER;
	}

	public void setUser(String sMTP_AUTH_USER) {
		SMTP_AUTH_USER = sMTP_AUTH_USER;
	}

	public void setPW(String sMTP_AUTH_PWD) {
		SMTP_AUTH_PWD = sMTP_AUTH_PWD;
	}

	public String getHostName() {
		return SMTP_HOST_NAME;
	}

	public void setHostName(String sMTP_HOST_NAME) {
		SMTP_HOST_NAME = sMTP_HOST_NAME;
	}

	public int getPort() {
		return SMTP_HOST_PORT;
	}

	public void setPort(int sMTP_HOST_PORT) {
		SMTP_HOST_PORT = sMTP_HOST_PORT;
	}
	
	public String getRecipient() {
		return SMTP_RECIPIENT;
	}

	public void setRecipient(String sMTP_RECIPIENT) {
		SMTP_RECIPIENT = sMTP_RECIPIENT;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}