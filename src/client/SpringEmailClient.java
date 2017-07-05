package client;

import java.awt.Font;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class SpringEmailClient {

	public static void main(String[] args){
		try
		{
			//Font f = new Font("calibri", Font.PLAIN, 14);
			//UIManager.put("Menu.font", f);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			@SuppressWarnings("unused")
			GUIController x = new GUIController();
			//GUI y = new GUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


