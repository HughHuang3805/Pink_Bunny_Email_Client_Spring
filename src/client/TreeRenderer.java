package client;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if (leaf && isInbox(value)) {
			setIcon(new ImageIcon("icons/inboxicon.png"));
			//setToolTipText("This book is in the Tutorial series.");
		} else if(leaf && isDraft(value)){
			setIcon(new ImageIcon("icons/writeicon.png"));
		} else if(leaf && isSent(value)){
			setIcon(new ImageIcon("icons/senticon.png"));
		} else if(leaf && isSpam(value)){
			setIcon(new ImageIcon("icons/spamicon.png"));
		} else{
			setIcon(new ImageIcon("icons/emailicon.png")); //no tool tip
		}

		return this;
	}

	protected boolean isInbox(Object value) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode)value;
		String nodeInfo =
				(String) (node.getUserObject());
		String title = nodeInfo.toString();
		if (title.indexOf("Inbox") >= 0) {
			return true;
		}
		return false;
	}
	
	protected boolean isDraft(Object value) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode)value;
		String nodeInfo =
				(String) (node.getUserObject());
		String title = nodeInfo.toString();
		if (title.indexOf("Draft") >= 0) {
			return true;
		}
		return false;
	}
	
	protected boolean isSent(Object value) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode)value;
		String nodeInfo =
				(String) (node.getUserObject());
		String title = nodeInfo.toString();
		if (title.indexOf("Sent") >= 0) {
			return true;
		}
		return false;
	}
	
	protected boolean isSpam(Object value) {
		DefaultMutableTreeNode node =
				(DefaultMutableTreeNode)value;
		String nodeInfo =
				(String) (node.getUserObject());
		String title = nodeInfo.toString();
		if (title.indexOf("Spam") >= 0) {
			return true;
		}
		return false;
	}
}