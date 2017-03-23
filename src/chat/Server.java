package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private ServerSocket server;
	
	private List<PrintWriter> allOut;
	
	public Server() throws IOException {
		server = new ServerSocket(8088);
		
		allOut = new ArrayList<PrintWriter>();
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("Waiting for client connection.....");
				Socket socket = server.accept();
				System.out.println("A client is connected!");	
				
				ClientHander hander = new ClientHander(socket);
				Thread thread = new Thread(hander);
				thread.start();		
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String message) {
		synchronized (allOut) {
			for(PrintWriter o : allOut) {
				o.println(message);
			}
		}
	}
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public class ClientHander implements Runnable {
		private Socket socket;
		private String host;
		
		public ClientHander(Socket socket) {
			this.socket = socket;
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
		}
		@Override
		public void run() {
			PrintWriter pw = null;
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in,"UTF-8");
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw,true);
				
				synchronized (allOut) {
					allOut.add(pw);
				}
				sendMessage(host+"On line！ Current online"+allOut.size());
				
				String message = null;
				while((message = br.readLine()) != null) {
					sendMessage(host+"say"+message);
				}
			} catch (Exception e) {
				
			} finally {
				synchronized (allOut) {
					allOut.remove(pw);
				}
				sendMessage(host+"Off the assembly line, Current online"+allOut.size());
				if(socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}

}
