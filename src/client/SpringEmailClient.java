package client;

import java.io.IOException;


public class SpringEmailClient {

	public static void main(String[] args){
		try
		{
			@SuppressWarnings("unused")
			GUIController x = new GUIController(new GUI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


