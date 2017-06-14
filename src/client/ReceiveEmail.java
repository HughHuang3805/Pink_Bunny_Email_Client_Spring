package client;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class ReceiveEmail{  

	static String host = "imap.gmail.com ";
	String mailStoreType = "imap";  
	static String username = "";  
	static String password = "";
	
	public static void receiveEmail() throws Exception {  
		try {  
			//1) get the session object  
			Properties props2=System.getProperties();

			props2.setProperty("mail.store.protocol", "imaps");
			//props2.put("mail.imaps.ssl.trust", "*");
			// I used imaps protocol here

			Session session2=Session.getDefaultInstance(props2, null);


				@SuppressWarnings("unused")
				Store store=session2.getStore("imaps");


			//2) create the POP3 store object and connect with the pop server  
				Store emailStore=session2.getStore("imaps");
			emailStore.connect(host, username, password);  

			//3) create the folder object and open it  
			Folder emailFolder = emailStore.getFolder("INBOX");  
			emailFolder.open(Folder.READ_ONLY);  

			//4) retrieve the messages from the folder in an array and print it  
			Message[] messages = emailFolder.getMessages();  
			for (int i = messages.length - 1; i > messages.length - 2 ; i--) {  
				Message message = messages[i];  
				/*System.out.println("---------------------------------");  
				System.out.println("Email Number " + (i + 1));  
				System.out.println("Subject: " + message.getSubject());  
				System.out.println("From: " + message.getFrom()[0]);  
				Scanner s;
				s = new Scanner(message.getInputStream()).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : " ";
				System.out.println(result);
				encryptedEmail.println(result);  
				encryptedEmail.close();
				*/
				getMultipart(message);
				TestBCOpenPGP x = new TestBCOpenPGP();
				x.decrypt();//once the cipher-text.dat is downloaded, start to decrypt
			}  

			//5) close the store and folder objects  
			emailFolder.close(false);  
			emailStore.close();  

		} catch (NoSuchProviderException e) {e.printStackTrace();}   
		catch (MessagingException e) {e.printStackTrace();}  
		catch (IOException e) {e.printStackTrace();}  
	}  

	public static void getMultipart(Message message) throws IOException, MessagingException{//download the cipher-text.dat
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
	
	public static void main(String[] args) throws Exception {  


		//receiveEmail(host, mailStoreType, username, password);  

	}  
} 
