package connection;

import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.message.MapMessage;
import org.springframework.messaging.simp.stomp.StompSession;

public class Request {
	private StompSession session;
	private MapMessage message;
	private String path;
	
	public Request(StompSession session) {
		this.session = session;
		this.message = new MapMessage();
		this.path = "";
	}
	
	public Request(StompSession session, String path) {
		this.session = session;
		this.message = new MapMessage();
		this.path = path;
	}
	
	public Request(StompSession session, MapMessage message) {
		this.session = session;
		this.message = message;
		this.path = "";
	}
	
	public Request(StompSession session, HashMap<String, String> map) {
		this.session = session;
		this.message = new MapMessage(map);
		this.path = "";
	}
	
	public void setSession(StompSession session) {
		this.session = session;
	}
	
	public void setMessage(MapMessage message) {
		this.message = message;
	}
	
	public void setMessage(HashMap<String, String> map) {
		this.message = new MapMessage(map);
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void addField(String fieldKey, String fieldValue) {
		this.message.put(fieldKey, fieldValue);
	}
	
	public void sendMessage() {
		if(this.path != null && !this.path.equals("")) {
			session.send(this.path, message.asString("JSON"));
		
			System.out.println("\nSend Message:");
			this.printFields();
		}
	}
	
	public void sendMessage(String path) {
		if(path != null && !path.equals("")) {
			session.send(path, message.asString("JSON"));
		
			System.out.println("\nSend Message:");
			this.printFields();
		}
	}
	
	public void printFields() {
		Set<String> keySet = this.message.getData().keySet();
		
		for(String key : keySet) {
			String value = this.message.get(key);
			
			System.out.println(key + ": " + value);
		}
	}
}
