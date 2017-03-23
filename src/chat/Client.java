package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	
	public Client() throws UnknownHostException, IOException {
		System.out.println("Connecting to server......");
		socket = new Socket("localhost", 8088);
		System.out.println("Connect to server successfully！");
	}
	public void start() {
		try {
			Scanner scan = new Scanner(System.in);
			
			OutputStream out = socket.getOutputStream();
			
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw,true);
			
			ServerHandler handler = new ServerHandler();
			Thread thread = new Thread(handler);
			thread.start();
			
			String line = null;
			System.out.println("Start chatting！");
			while(true) {
				line = scan.nextLine();
				pw.println(line);
			}
 			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class ServerHandler implements Runnable {

		@Override
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in,"UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String message = null;
				while((message = br.readLine()) != null) {
					System.out.println(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.start();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Client boot failure");
		}	
	}
}
