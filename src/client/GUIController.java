package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

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

		case "Log in":
			if(emailServer.isSmtpLoggedIn()){
				JOptionPane.showMessageDialog(myGui, "Already logged in", "oops ...", JOptionPane.INFORMATION_MESSAGE);
			} else{
				myGui.setLoginFrame(emailServer);
			}
			break;

		case "Get new messages":
			try {
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

		case "Remove account":
			myGui.setRemoveAccountDialog(emailServer);
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
				GUIController.userEmailObjects.elementAt(i).setSmtpLoggedIn(false);
				oos.writeObject(GUIController.userEmailObjects.elementAt(i));
			}
			//oos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
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
		if(tree.getLastSelectedPathComponent() != null){
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			//e.getClickCount() == 1 
			/*if(row != 0 && tree.getLastSelectedPathComponent().toString() == "Secure write"){
				emailServer = emailObjectMap.get(node.getRoot().toString());
				if(emailServer.isSmtpLoggedIn()){
					myGui.setSecureWritePanel(userEmails, emailServer);
				} else{
					JOptionPane.showMessageDialog(myGui, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
					//myGui.setLoginFrame(emailServer);
				}
				tree.clearSelection();
				//System.out.println("double clicked");
			} else if(row != 0 && tree.getLastSelectedPathComponent().toString() == "Write"){
				emailServer = emailObjectMap.get(node.getRoot().toString());
				if(emailServer.isSmtpLoggedIn()){
					myGui.setWriteFrame(userEmails, emailServer);
				} else{
					JOptionPane.showMessageDialog(myGui, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
					//myGui.setLoginFrame(emailServer);
				}
				tree.clearSelection();
				//System.out.println("double clicked");
			} else if(row != 0 && tree.getLastSelectedPathComponent().toString() == "Inbox"){
				try {
					//System.out.println("Root:" + node.getRoot().toString());
					emailServer = emailObjectMap.get(node.getRoot().toString());
					//System.out.println("email server is null" + emailServer == null);
					if(emailServer.isSmtpLoggedIn()){
						myGui.setDisplayRightPanel(emailServer);
						//System.out.println(emailServer.getEmailTable() == null);
					} else {
						JOptionPane.showMessageDialog(myGui, "Please log in first.", "oops ...", JOptionPane.WARNING_MESSAGE);
						//myGui.setLoginFrame(emailServer);
					}
					tree.clearSelection();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else */
			if(SwingUtilities.isRightMouseButton(e)){
				if(node.isRoot()) {
					emailServer = emailObjectMap.get(node.getRoot().toString());
					myGui.getEmailPopupMenu().show(tree, e.getX(), e.getY());
					String email = node.getUserObject().toString();
					//myGui.emailTextField = new JTextField(email);
					//emailServer = emailObjectMap.get(email);
					System.out.println(email);
					emailServer.setUsername(email);
				}
				tree.clearSelection();
			} 

			if(row == -1) //When user clicks on the "empty surface"
				tree.getSelectionModel().clearSelection();
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
		int size = myGui.getTrees().size();
		Vector<JTree> trees = myGui.getTrees();
		for(int i = 0; i < size; i++){
			JTree treeRoot = trees.elementAt(i);
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
}
