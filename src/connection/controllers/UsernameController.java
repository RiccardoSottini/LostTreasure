package connection.controllers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class UsernameController {
	private final Launcher launcher;
	private final StompSession session;
	private String username;
	private String user_token;
	
	private String error_message;
	
	public UsernameController(Launcher launcher, String username) {
		this.launcher = launcher;
		this.session = launcher.getSession();
		this.user_token = launcher.getUserToken();
		this.username = username;
	}
	
	public boolean sendUsername() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/username/" + sessionID);
		
		Request request = new Request(session, "/app/username/" + sessionID);
		request.addField("username", this.username);
		request.addField("user_token", this.user_token);
		request.sendMessage();
		
		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				this.username = response_list.get("username");
				this.user_token = response_list.get("user_token");
				return true;
			} else {
				this.error_message = response_list.get("error_message");
			}
		} catch (Exception e) {
			this.error_message = e.getMessage();
		}
		
		return false;
	}
	
	public String getToken() {
		return this.user_token;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getError() {
		return this.error_message;
	}
}
