package client;

import java.awt.Component;
import java.awt.Font;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	SecureMailService emailServer;
	int messageLength;
	Message message;
	transient Message[] messages;
	Component cellComponent;
	boolean isSeen = false;
	
	public TableRenderer(SecureMailService emailServer) throws MessagingException{
		this.emailServer = emailServer;
		messages = emailServer.getInboxFolder().getMessages();
		messageLength = messages.length;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		try {
			message = messages[messageLength - row - 1];//get the specific message
			isSeen = message.isSet(Flags.Flag.SEEN);//check if the message is read
			if(!isSeen){//if it is not read, make the font bold
				cellComponent.setFont(new Font("Serif", Font.BOLD, 16));
			} else{//if it is read, make the font normal
				cellComponent.setFont(new Font("Serif", Font.PLAIN, 16));
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
}
