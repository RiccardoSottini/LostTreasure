package connection.controllers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;

public class LoginController {
	private final StompSession session;
	private final String username;
	private final String password_hash;
	
	private String user_token;
	private String user_name;
	private String error_message;
	
	public LoginController(StompSession session, String username, String password) {
		this.session = session;
		this.username = username;
		this.password_hash = this.passwordHash(password);
	}
	
	public boolean sendLogin() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/login/" + sessionID);
		
		Request request = new Request(session, "/app/login/" + sessionID);
		request.addField("username", this.username);
		request.addField("password", this.password_hash);
		request.sendMessage();
		
		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				this.user_token = response_list.get("user_token");
				this.user_name = response_list.get("user_name");
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
		return this.user_name;
	}
	
	public String getError() {
		return this.error_message;
	}
	
	private String passwordHash(String password) {
		String password_hash = "";
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] password_digest = md.digest(password.getBytes("UTF-8"));
			
			StringBuilder sb = new StringBuilder(2 * password_digest.length);
			for(byte b : password_digest) {
				sb.append(String.format("%02x", b&0xff));
			}
			
			password_hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return password_hash;
	}
}
