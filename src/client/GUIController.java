package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Hashtable;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bouncycastle.openpgp.PGPException;

public class GUIController implements ActionListener, MouseListener, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GUI myGui;
	SecureMailService emailServer;
	private static String objectFileName = "config.ser";
	public static Vector<SecureMailService> userEmailObjects = new Vector<SecureMailService>();
	public static Hashtable<String, SecureMailService> emailObjectMap = new Hashtable<String, SecureMailService>();

	public final static Hashtable<String, String> smtpServers = new Hashtable<String, String>() {/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		{
			put("GMAIL", "smtp.gmail.com");
			put("OUTLOOK", "smtp-mail.outlook.com");
			put("OFFICE365", "smtp.office365.com");
			put("YAHOO", "smtp.mail.yahoo.com");
			put("AOL", "smtp.aol.com");
			put("HOTMAIL", "smtp-mail.outlook.com");
		}
	};

	public final static Hashtable<String, String> portNumbers = new Hashtable<String, String>() {/**
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

	public final static Hashtable<String, String> imapServers = new Hashtable<String, String>() {/**
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
		readUserEmailObjects();
		myGui = new GUI(this, this);
		myGui.setButtonListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String buttonName = e.getActionCommand();

		switch(buttonName){

		case "Add New Account":
			myGui.setAddAccountEmailFrame();
			break;

	/*	case "Log in":
			if(emailServer.isSmtpLoggedIn()){
				JOptionPane.showMessageDialog(myGui, "Already logged in", "oops ...", JOptionPane.INFORMATION_MESSAGE);
			} else{
				myGui.setLoginFrame(emailServer);
			}
			break;*/

		/*case "Get new messages":
			try {
				//interrupt the sleeping populateEmailTables, wake them up and let them work
				synchronized(emailServer){
					emailServer.notify();
					//myGui.populateEmailTable(emailServer);
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;*/

		/*case "Remove account":
			myGui.setRemoveAccountDialog(emailServer);
			break;*/

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

	public void readUserEmailObjects(){
		File f = new File(objectFileName);
		if(f.exists() && !f.isDirectory()) { 
			// do something
			try {
				FileInputStream fi = new FileInputStream(f);
				ObjectInputStream oi = new ObjectInputStream(fi);
				// Read objects

				while(true){
					try{
						SecureMailService emailServer;
						System.out.println("hi");
						emailServer = (SecureMailService) oi.readObject();
						System.out.println(emailServer.getUsername());
						userEmailObjects.add(emailServer);
						emailObjectMap.put(emailServer.getUsername(), emailServer);//maps an email with the appropriate email object
					} catch (EOFException e){
						break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				oi.close();
				fi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void writeUserEmailObject(){
		File objectFile = new File(objectFileName);//save objects before program closes
		try(ObjectOutputStream oos =
				new ObjectOutputStream(new FileOutputStream(objectFile))) {
			for(int i = 0; i < GUIController.userEmailObjects.size(); i++){
				// Write objects to file
				GUIController.userEmailObjects.elementAt(i).setSmtpLoggedIn(false);//make isLoggedIn false
				GUIController.userEmailObjects.elementAt(i).setMessages(null);//clear out messages
				DefaultTableModel model = (DefaultTableModel) GUIController.userEmailObjects.elementAt(i).emailTable.getModel();//remove what's in emailTable
				//model.setRowCount(0);
				//GUIController.userEmailObjects.elementAt(i).setEmailCounter(0);
				GUIController.userEmailObjects.elementAt(i).getInboxFolder().close(true);//close the email folder
				GUIController.userEmailObjects.elementAt(i).setInboxFolder(null);
				oos.writeObject(GUIController.userEmailObjects.elementAt(i));
			}
			//oos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		JTree tree = (JTree) e.getComponent();
		tree.getLastSelectedPathComponent();
		int row = tree.getRowForLocation(e.getX(), e.getY());
		//2 is the number of clicks, row != 0 means if it is not root, get the path of which the item is clicked
		DefaultMutableTreeNode node;
		node = (DefaultMutableTreeNode)
				tree.getLastSelectedPathComponent();
		if(SwingUtilities.isRightMouseButton(e) && tree.getRowForLocation(e.getX(), e.getY()) == 0){
			int index = tree.getRowForLocation(e.getX(), e.getY());
			System.out.println(e.getX() + " " + e.getY());
			TreePath selectedPath = tree.getPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(selectedPath);
			if(index > -1){
				tree.setSelectionRow(index);
			}
			node = (DefaultMutableTreeNode)
					tree.getModel().getRoot();
			emailServer = emailObjectMap.get(node.getRoot().toString());
			myGui.getEmailPopupMenu().show(tree, e.getX(), e.getY());
			String email = node.getUserObject().toString();
			System.out.println(email);
			emailServer.setUsername(email);
			tree.clearSelection();
		} 
		if(row == -1) //When user clicks on the "empty surface"
			tree.getSelectionModel().clearSelection();
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
}
