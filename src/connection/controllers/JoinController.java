package connection.controllers;

import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class JoinController {
	private final Launcher launcher;
	private final StompSession session;
	private final String user_token;
	private final String game_token;
	
	private String error_message;
	
	public JoinController(Launcher launcher, StompSession session, String user_token, String game_token) {
		this.launcher = launcher;
		this.session = session;
		this.user_token = user_token;
		this.game_token = game_token;
	}
	
	public boolean sendJoin() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/game/join/" + sessionID);
		
		Response responseList = new Response(session, "/response/game/list/" + user_token, new Object() {
			public void execute(HashMap<String, String> response_list) {
				if(response_list.get("game_start").equals("true")) {
					JoinController.this.getLauncher().startGame(response_list);
				} else {
					JoinController.this.getLauncher().updateList(response_list);
				}
			}
		});
		
		Request request = new Request(session, "/app/game/join/" + sessionID);
		request.addField("user_token", this.user_token);
		request.addField("game_token", this.game_token);
		request.sendMessage();

		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				this.launcher.setGameToken(response_list.get("game_token"));
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
