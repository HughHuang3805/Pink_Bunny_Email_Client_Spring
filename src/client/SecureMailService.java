package client;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.sql.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.bouncycastle.openpgp.PGPException;


public class SecureMailService {

	private String SMTP_HOST_NAME;
	private int SMTP_HOST_PORT;
	private String recipient;
	private String subject;
	private String emailType;
	private boolean smtpLoggedIn = false;

	private String imapHost = "";
	@SuppressWarnings("unused")
	private String mailStoreType = "imap";  
	private String username = "";  
	private String password = "";
	JFrame writeFrame, secureWriteFrame, loginFrame, yubikeyFrame;
	JTable emailTable;
	JPanel rightEmailContentPanel;
	private int emailID;

	public void encryptedSend() throws Exception{
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

		transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, username, password);
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	public void send(String host, String messageContent) throws Exception{
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
		message.setFrom(new InternetAddress(getUsername()));
		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(getRecipient()));
		message.setText(messageContent);

		transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, username, password);
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	public boolean connect() throws GeneralSecurityException{
		Properties props = new Properties();

		switch(emailType){

		case "GMAIL":
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			//props.put("mail.smtps.ssl.trust", "*");
			//props.put("mail.smtps.quitwait", "false");
			break;

		case "HOTMAIL":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;

		case "OUTLOOK":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			//props.put("mail.smtp.port", "587");
			break;

		case "OFFICE365":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;

		case "AOL":
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			break;

		case "YAHOO":
			props.put("mail.smtp.ssl.trust", "smtp.mail.yahoo.com");
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.host", SMTP_HOST_NAME);
			props.put("mail.smtps.auth", "true");
			//props.put("mail.smtps.ssl.trust", "*");
			//props.put("mail.smtps.quitwait", "false");
			break;

		default:
			break;
		}
		//System.out.println(host);
		Session mailSession = Session.getDefaultInstance(props);
		Transport transport;
		try {
			transport = mailSession.getTransport();
			System.out.print(SMTP_HOST_NAME + " " + SMTP_HOST_PORT + " " + username + " " + password);
			transport.connect
			(SMTP_HOST_NAME, SMTP_HOST_PORT, username, password);
			smtpLoggedIn = true;
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

	public void getEmailByNumber(int emailNumber) throws Exception {  
		try {  
			//1) get the session object  
			Properties props2=System.getProperties();

			props2.setProperty("mail.store.protocol", "imaps");
			//props2.put("mail.imaps.ssl.trust", "*");

			Session session2=Session.getDefaultInstance(props2, null);


			@SuppressWarnings("unused")
			Store store=session2.getStore("imaps");


			//2) create the POP3 store object and connect with the pop server  
			Store emailStore=session2.getStore("imaps");
			emailStore.connect(imapHost, username, password);  

			//3) create the folder object and open it  
			Folder emailFolder = emailStore.getFolder("INBOX");  
			emailFolder.open(Folder.READ_ONLY);  

			//4) retrieve the messages from the folder in an array and print it  
			Message[] messages = emailFolder.getMessages();  
			//Message message = messages[messages.length - 1 - emailNumber];  
			Message message = messages[messages.length - 1 - emailNumber]; 
			rightEmailContentPanel = new JPanel();


			@SuppressWarnings("resource")
			Scanner s = new Scanner(message.getInputStream()).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : " ";
			System.out.println(message.getContentType());

			JScrollPane emailScroll;
			rightEmailContentPanel.setLayout(new BorderLayout());
			if(message.isMimeType("text/html")){
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("text/html");
				editorPane.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);
				editorPane.setText(result);
				emailScroll = new JScrollPane(editorPane);
				rightEmailContentPanel.add(emailScroll, BorderLayout.CENTER);
				System.out.println(message.getContentType());
			} else if(message.isMimeType("text/plain")){
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("text/plain");
				//editorPane.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);
				//String x = URLDecoder.decode(result, "US-ASCII");
				editorPane.setText(result);
				emailScroll = new JScrollPane(editorPane);
				rightEmailContentPanel.add(emailScroll, BorderLayout.CENTER);
				System.out.println(message.getContentType());
			} else if(message.isMimeType("multipart/*")){
				MimeMultipart mp = (MimeMultipart) message.getContent();
				result = getTextFromMimeMultipart(mp);
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("multipart/ALTERNATIVE");
				editorPane.setText(result);
				emailScroll = new JScrollPane(editorPane);
				rightEmailContentPanel.add(emailScroll, BorderLayout.CENTER);
				System.out.println(message.getContentType());
			}
			//emailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


			/*System.out.println("---------------------------------");  
			System.out.println("Email Number " + (emailNumber + 1));  
			System.out.println("Subject: " + message.getSubject());  
			System.out.println("From: " + message.getFrom()[0]); 
			message.getReceivedDate();
			Scanner s;
			s = new Scanner(message.getInputStream()).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : " ";
			System.out.println(result);*/
			//encryptedEmail.println(result);  
			//encryptedEmail.close();

			/*getMultipart(message);
			TestBCOpenPGP x = new TestBCOpenPGP();
			x.decrypt();//once the cipher-text.dat is downloaded, start to decrypt
			s.close();*/

			//5) close the store and folder objects  
			s.close();
			emailFolder.close(false);  
			emailStore.close(); 

		} /*catch (NoSuchProviderException e) {e.printStackTrace();} */  
		catch (MessagingException e) {e.printStackTrace();}  
		//catch (IOException e) {e.printStackTrace();}  
	} 

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        } else if (bodyPart.isMimeType("multipart/*")){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}

	/*public void getEmailByNumber(int emailNumber) throws Exception {  
		try {  
			//1) get the session object  
			Properties props2=System.getProperties();

			props2.setProperty("mail.store.protocol", "imaps");
			//props2.put("mail.imaps.ssl.trust", "*");

			Session session2=Session.getDefaultInstance(props2, null);


			@SuppressWarnings("unused")
			Store store=session2.getStore("imaps");


			//2) create the POP3 store object and connect with the pop server  
			Store emailStore=session2.getStore("imaps");
			emailStore.connect(imapHost, username, password);  

			//3) create the folder object and open it  
			Folder emailFolder = emailStore.getFolder("INBOX");  
			emailFolder.open(Folder.READ_ONLY);  

			//4) retrieve the messages from the folder in an array and print it  
			Message[] messages = emailFolder.getMessages();  
			for (int i = messages.length - 1; i >= 0 ; i--) {  
				Message message = messages[i];  
				System.out.println("---------------------------------");  
				System.out.println("Email Number " + (i + 1));  
				System.out.println("Subject: " + message.getSubject());  
				System.out.println("From: " + message.getFrom()[0]); 
				message.getReceivedDate();
				Scanner s;
				s = new Scanner(message.getInputStream()).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : " ";
				System.out.println(result);
				//encryptedEmail.println(result);  
				//encryptedEmail.close();

				getMultipart(message);
				TestBCOpenPGP x = new TestBCOpenPGP();
				x.decrypt();//once the cipher-text.dat is downloaded, start to decrypt
				s.close();
			}  

			//5) close the store and folder objects  
			emailFolder.close(false);  
			emailStore.close();  

		} catch (NoSuchProviderException e) {e.printStackTrace();}   
		catch (MessagingException e) {e.printStackTrace();}  
		catch (IOException e) {e.printStackTrace();}  
	} */

	public Message[] getMessages() throws Exception {  
		try {  
			//1) get the session object  
			Properties props2=System.getProperties();

			props2.setProperty("mail.store.protocol", "imaps");
			//props2.put("mail.imaps.ssl.trust", "*");

			Session session2=Session.getDefaultInstance(props2, null);

			@SuppressWarnings("unused")
			Store store=session2.getStore("imaps");

			//2) create the POP3 store object and connect with the pop server  
			Store emailStore=session2.getStore("imaps");
			emailStore.connect(imapHost, username, password);  

			//3) create the folder object and open it  
			Folder emailFolder = emailStore.getFolder("INBOX");  
			emailFolder.open(Folder.READ_ONLY);  

			//4) retrieve the messages from the folder in an array and print it  
			Message[] messages = emailFolder.getMessages();  

			//5) close the store and folder objects  
			//emailFolder.close(false);  
			//emailStore.close();  
			return messages;
		} catch (MessagingException e) {
			e.printStackTrace();
		}  
		return null;
	} 

	public void getMultipart(Message message) throws IOException, MessagingException{//download the cipher-text.dat
		Multipart multiPart = (Multipart) message.getContent();
		String attachFiles = "";
		@SuppressWarnings("unused")
		String messageContent = "";
		int numberOfParts = multiPart.getCount();
		for (int partCount = 0; partCount < numberOfParts; partCount++) {
			MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
			if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
				// this part is attachment
				String fileName = part.getFileName();
				attachFiles += fileName + ", ";
				part.saveFile(fileName);
				//System.out.println(fileName);
			} else {
				// this part may be the message content
				messageContent = part.getContent().toString();
			}
		}

		if (attachFiles.length() > 1) {
			attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSmtpLoggedIn() {
		return smtpLoggedIn;
	}

	public void setSmtpLoggedIn(boolean smtpLoggedIn) {
		this.smtpLoggedIn = smtpLoggedIn;
	}

	public String getSMTPServer() {
		return SMTP_HOST_NAME;
	}

	public void setSMTPServer(String sMTP_HOST_NAME) {
		SMTP_HOST_NAME = sMTP_HOST_NAME;
	}

	public int getPort() {
		return SMTP_HOST_PORT;
	}

	public void setPort(int sMTP_HOST_PORT) {
		SMTP_HOST_PORT = sMTP_HOST_PORT;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String sMTP_RECIPIENT) {
		recipient = sMTP_RECIPIENT;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getImapHost() {
		return imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	public JTable getEmailTable() {
		return emailTable;
	}

	public void setEmailTable(JTable emailTable) {
		this.emailTable = emailTable;
	}

	public JFrame getWriteFrame() {
		return writeFrame;
	}

	public void setWriteFrame(JFrame writeFrame) {
		this.writeFrame = writeFrame;
	}

	public JFrame getSecureWriteFrame() {
		return secureWriteFrame;
	}

	public void setSecureWriteFrame(JFrame secureWriteFrame) {
		this.secureWriteFrame = secureWriteFrame;
	}

	public JFrame getLoginFrame() {
		return loginFrame;
	}

	public void setLoginFrame(JFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public JFrame getYubikeyFrame() {
		return yubikeyFrame;
	}

	public void setYubikeyFrame(JFrame yubikeyFrame) {
		this.yubikeyFrame = yubikeyFrame;
	}

	public int getEmailID() {
		return emailID;
	}

	public void setEmailID(int emailID) {
		this.emailID = emailID;
	}

	public JPanel getRightEmailContentPanel() {
		return rightEmailContentPanel;
	}

	public void setRightEmailContentPanel(JPanel rightEmailContentPanel) {
		this.rightEmailContentPanel = rightEmailContentPanel;
	}
}