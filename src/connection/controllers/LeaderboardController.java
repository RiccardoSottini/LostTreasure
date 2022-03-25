package connection.controllers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class LeaderboardController {
	private final Launcher launcher;
	private final String user_token;
	private final StompSession session;
	
	private String error_message;
	private LinkedHashMap<String, Integer> leaderboard_list;
	
	public LeaderboardController(Launcher launcher, String user_token) {
		this.launcher = launcher;
		this.user_token = user_token;
		this.session = this.launcher.getSession();
		
		this.leaderboard_list = new LinkedHashMap<String, Integer>();
	}
	
	public boolean sendUpdate() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/leaderboard/" + sessionID);
		
		Request request = new Request(session, "/app/leaderboard/" + sessionID);
		request.addField("user_token", this.user_token);
		request.sendMessage();
		
		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				int list_size = Integer.parseInt(response_list.get("list_size"));
				
				for(int index = 1; index <= list_size; index++) {
					String username = response_list.get("list_username_" + index);
					Integer score = Integer.parseInt(response_list.get("list_score_" + index));
					
					this.leaderboard_list.put(username, score);
				}
				
				return true;
			} else {
				this.error_message = response_list.get("error_message");
			}
		} catch (Exception e) {
			this.error_message = e.getMessage();
		}
		
		return false;
	}
	
	public LinkedHashMap<String, Integer> getList() {
		return this.leaderboard_list;
	}
	
	public String getError() {
		return this.error_message;
	}
}