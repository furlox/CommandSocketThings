import java.net.*;
import java.io.*;

public class CommandClient extends Thread {
	
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
	    System.out.println("client: "+line);
	}
	
	public static void main(String[] args) {
		int port = 60001;
		String servername = "127.0.0.1";
		try 
		{
			log("trying to connect to server");
			Socket client = new Socket(servername, port);
			log("connected to "+client.getRemoteSocketAddress());
			send(client, "tarang@pass2@makevisible");
			send(client, "tarang@pass2@displayvisible");
			//
			String buffer = recv(client);
			if(buffer.length()>0){
				String chunks[] = buffer.split("@");
				try {
					if(chunks[2].equals("ropenbrowser")) {
						log("received command to open browser");
						Process p1 = Runtime.getRuntime().exec("firefox");
					}
					if(chunks[2].equals("ropennotepad")) {
					
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else log("nothing to show from server");
		}
		catch (ConnectException e) {
			log("error: server refused connection");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
