package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class SpringEmailClient {

	public static void main(String[] args){
		try
		{
			@SuppressWarnings("unused")
			GUIController x = new GUIController(new GUI());
			/*Socket emailClient =  new Socket("192.168.0.137", 465);
			out = new PrintWriter(emailClient.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(emailClient.getInputStream()));*/
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


