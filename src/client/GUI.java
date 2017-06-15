package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	Vector<JMenuItem> menuItems = new Vector<JMenuItem>();
	JTextArea textArea = new JTextArea();
	JPanel emailPanel = new JPanel();
	JPanel passwordPanel = new JPanel();
	JPanel yubikeyPanel = new JPanel();
	JPanel buttonPanel;
	JButton signInButton = new JButton("Sign-in");
	JButton cancelButton = new JButton("Cancel");;
	JButton nextButton = new JButton("Next");
	JTextField emailText;
	JPasswordField passwordText, yubikeyText;
	JScrollPane jsp;
	JPanel panel;
	JMenu menu1;
	JTextArea x;
	JScrollPane jspForBody;

	public GUI(){
		setTitle("Pink Bunny E-mail Client");
		setSize(1250, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMenuItems();
		setLocationRelativeTo(null);
		setEmailPanel();
		getContentPane().setBackground(new Color(51, 102, 255));
		setVisible(true);
	}

	//adds listeners to each of the buttons
	public void setButtonListener(ActionListener a){
		signInButton.addActionListener(a);
		cancelButton.addActionListener(a);
		nextButton.addActionListener(a);
		for(JMenuItem x : menuItems){
			x.addActionListener(a);
		}
	}

	public void setMenuItems(){
		JMenuBar menuBar = new JMenuBar();
		//JMenuItem item1, item2, item3, item4, item5, item6;
		menu1 = new JMenu("File");

		/*	menuItems.add(item1);
		menuItems.add(item2);
		menuItems.add(item3);
		 */
		menu1.add(new JMenuItem("Get All New Messages"));
		//menu1.addSeparator();
		menu1.add(new JMenuItem("Write"));
		menu1.addSeparator();
		menu1.add(new JMenuItem("Exit"));
		//menu1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));

		menuBar.add(menu1);
		//menuBar.add(menu2);

		menu1.setEnabled(false);
		setJMenuBar(menuBar);
	}

	public void clearInsertPanel(){
		panel.setVisible(false);
	}

	public void setEmailBodyTextArea(){

		if(x == null)
			x = new JTextArea("Enter your email body ...");
		else
			x.setText("Enter your email body ...");
		x.setEditable(true);
		x.setFont(new Font("Serif", Font.PLAIN, 60));
		jspForBody = new JScrollPane(x);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(jspForBody, BorderLayout.CENTER);
		panel.repaint();

		add(panel);
		repaint();
		setVisible(true);
	}

	public void setSendDebugTextArea(){
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader("debug.txt"));
			String line = null;
			String debug = "";
			while ((line = br.readLine()) != null) {
				debug = debug + line + "\n";
			}
			x.setText(debug);
			x.setEditable(false);

			repaint();
			setVisible(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setReceivedEmailTextArea(String message){
		x.setText(message);
		x.setEditable(false);

		repaint();
		setVisible(true);
	}

	public void setEmailPanel(){//ask for email password and yubikey in the login screen

		emailPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;

		//Email label and email textfield
		JLabel emailLabel = new JLabel("Email: ");
		emailLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;//position in tje column
		cs.gridy = 0;//position in the row
		cs.gridwidth = 1;
		cs.weightx = 1.0;//a non-0 value such as 1.0 for most fields and 0 for fields whose size you don't want changed if the GUI changes size
		cs.weighty = 0;
		emailPanel.add(emailLabel, cs);

		emailText = new JTextField(13);
		emailText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		emailPanel.add(emailText, cs);

		emailPanel.setBorder(new LineBorder(Color.GRAY));//make a border for login panel

		//create next and cancel button
		nextButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);

		//the default button that will be clicked when press "enter"
		getRootPane().setDefaultButton(nextButton);
		add(emailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public void setPasswordPanel(){
		passwordPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;
		//Password label and password textfield
		JLabel passwordLabel = new JLabel("Password: ");
		passwordLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		passwordPanel.add(passwordLabel, cs);

		passwordText = new JPasswordField(13);
		passwordText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		passwordPanel.add(passwordText, cs);
		
		passwordPanel.setBorder(new LineBorder(Color.GRAY));
		
		signInButton.setFont(new Font("Serif", Font.PLAIN, 30));
		cancelButton.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel = new JPanel();//create a panel for the buttons
		buttonPanel.add(signInButton);
		buttonPanel.add(cancelButton);
		
		getRootPane().setDefaultButton(signInButton);
		add(passwordPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public void setYubikeyPanel(){
		yubikeyPanel.setLayout(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();//constraints
		cs.fill = GridBagConstraints.HORIZONTAL;
		//Yubikey label and yubikey password
		JLabel yubikeyLabel = new JLabel("YubiKey: ");
		yubikeyLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 0;
		cs.gridy = 2;
		cs.gridwidth = 1;
		cs.weightx = 1.0;
		cs.weighty = 0;
		yubikeyPanel.add(yubikeyLabel, cs);

		yubikeyText = new JPasswordField(13);
		yubikeyText.setFont(new Font("Serif", Font.PLAIN, 40));
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 2;
		cs.weightx = 1.0;
		cs.weighty = 0;
		yubikeyPanel.add(yubikeyText, cs);
		
		getRootPane().setDefaultButton(nextButton);
		add(yubikeyPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack(); //let layout managers in charge of the frame size
		setResizable(false);
		setVisible(true);
	}

	public String getEmail(){
		//get rid of any space in the username
		return emailText.getText().replaceAll("\\s+","");
	}

	public String getPassword(){
		String passText = new String(passwordText.getPassword());
		return passText;
	}

	public String getYubikey(){
		String passText = new String(yubikeyText.getPassword());
		return passText;
	}

	public void clearGUI(){
		textArea.setText(null);
	}

}
