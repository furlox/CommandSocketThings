import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.SocketException;
import java.util.Map;
import java.util.HashMap;

public class LocalSocket extends Thread {
	private static Map<Integer,Socket> id_to_sock = new HashMap<Integer,Socket>();
	private Socket localSock;
	private long id;
	// format for permissions:
	// browser,notepad,musicplayer, ... <remote samelist>
	private String AuthList[][] =
		{
			{"furlox","pass1", "openbrowser|opennotepad|openmusicplayer|makevisible|hide"},
			{"tarang","pass2","openbrowser|opennotepad|openmusicplayer|makevisible|hide|displayvisible"},
			{"sunil","pass3","110110"}
		};

	public static void send(Socket s, String msg) throws IOException {
		OutputStream os1 = s.getOutputStream();
		DataOutputStream out = new DataOutputStream(os1);
		out.writeUTF(msg);
	}
	
	public boolean can_execute(String[] userinfo, String command) {
		if(userinfo[2].indexOf(command) != -1) {
			log("command can be executed");
			return true;
		}
		else{
			log("warning: user not allowed to execute this command");
			return false;
		}
	}
	
	public static String recv(Socket s) throws IOException {
		InputStream is1 = s.getInputStream();
		DataInputStream in = new DataInputStream(is1);
		return in.readUTF();
	}
	
	public void log(Object line) {
	    System.out.println("local["+id+"]: "+line);
	}
	
	public void assign_id(long k) {
		id = k;
	}
	
	public LocalSocket(Socket s) {
		localSock = s;
	}
	public void run()
	{
		while(true) {
			if(localSock.isClosed() || !localSock.isConnected()) {
				log("connection closed");
			}
			try
			{
				String buffer = recv(localSock);
				if(buffer.length()>0) {
					log("");
					log(buffer);
					String chunks[] = buffer.split("@");
					//log(chunks[0]);
					//log(chunks[1]);
					//log(chunks[2]);
					for(int i=0; i<3; i++) {
						if( AuthList[i][0].equals(chunks[0]) && AuthList[i][1].equals(chunks[1]) ) {
							log("user validated");
							if(can_execute(AuthList[i], chunks[2])){
								if(chunks[2].equals("makevisible")){
									id_to_sock.put((int)(long)id,localSock);
									log("added client to list of visible nodes");
									log("remotesock addr. = "+id_to_sock.get((int)(long)id).getRemoteSocketAddress());
								}
								if(chunks[2].equals("displayvisible")){
									for(Integer key: id_to_sock.keySet()){
										log(key+" "+id_to_sock.get(key));
									}
								}
								if(chunks[2].equals("hide")){
									id_to_sock.remove(id);
								}
							}
						}
						if( AuthList[i][0].equals(chunks[0]) && !AuthList[i][1].equals(chunks[1]) ) {
							log("warning: failed login attempt for user "+AuthList[i][0]);
						}
					}
				}
				//
				send(localSock,"tarang@pass2@ropenbrowser");
			}
			catch (SocketException e){
				
			}
			catch (EOFException e) {
				break;
			}
			catch (SocketTimeoutException s_exception) {
				log("local"+id+":"+"error: socket timed out");
				break;
			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}