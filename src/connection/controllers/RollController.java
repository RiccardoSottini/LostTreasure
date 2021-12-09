package connection.controllers;

import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class RollController {
	private final Launcher launcher;
	private final StompSession session;
	private final String user_token;
	private final String game_token;
	
	private String error_message;
	
	public RollController(Launcher launcher) {
		this.launcher = launcher;
		this.session = launcher.getSession();
		this.user_token = launcher.getUserToken();
		this.game_token = launcher.getGameToken();
	}
	
	public boolean sendRoll() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/game/roll/" + sessionID);
		
		Request request = new Request(session, "/app/game/roll/" + sessionID);
		request.addField("user_token", this.user_token);
		request.addField("game_token", this.game_token);
		request.sendMessage();
		
		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				return true;
			} else {
				this.error_message = response_list.get("error_message");
			}
		} catch (Exception e) {
			this.error_message = e.getMessage();
		}
		
		return false;
	}
	
	public Launcher getLauncher() {
		return this.launcher;
	}
	
	public String getError() {
		return this.error_message;
	}
}
