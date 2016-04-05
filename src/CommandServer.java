import java.net.*;
import java.io.*;
import java.net.SocketTimeoutException;

public class CommandServer extends Thread {

	private ServerSocket serverSocket;
	
	public CommandServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}
	
	public static void send(Socket s, String msg) throws IOException {
		OutputStream os1 = s.getOutputStream();
		DataOutputStream out = new DataOutputStream(os1);
		out.writeUTF(msg);
	}
	
	public static String recv(Socket s) throws IOException {
		InputStream is1 = s.getInputStream();
		DataInputStream in = new DataInputStream(is1);
		return in.readUTF();
	}
	
	public static void log(Object line) {
	    System.out.println("server: "+line);
	}
	
	public void run()
	{
		while(true) {
			try{
				/* wait for a client to connect */
				log("waiting for client on port " + serverSocket.getLocalPort());
				Socket server = serverSocket.accept();
				/* handle connection */
				log("");
				log("connected to "+ server.getRemoteSocketAddress());
				LocalSocket t = new LocalSocket(server);
				log("spawning thread "+t.getId()+" to handle this client");
				log("");
				t.assign_id(t.getId());
				t.start();
				
			}
			catch (SocketTimeoutException e) {
				log("no clients requested connection");
			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	public static void main(String[] args) {
		int port = 60001;
		try 
		{
			Thread t = new CommandServer(port);
			t.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
