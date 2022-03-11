package connection.controllers;

import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class MoveController {
	private final Launcher launcher;
	private final StompSession session;
	private final String user_token;
	private final String game_token;
	
	private final int cell_index;
	private final String cell_type;
	
	private String error_message;
	
	public MoveController(Launcher launcher, int cell_index, String cell_type) {
		this.launcher = launcher;
		this.session = launcher.getSession();
		this.user_token = launcher.getUserToken();
		this.game_token = launcher.getGameToken();
		
		this.cell_index = cell_index;
		this.cell_type = cell_type;
	}
	
	public boolean sendMove() {
		String sessionID = session.getSessionId();
		
		Response response = new Response(session, "/response/game/move/" + sessionID);
		
		Request request = new Request(session, "/app/game/move/" + sessionID);
		request.addField("user_token", this.user_token);
		request.addField("game_token", this.game_token);
		request.addField("cell_index", Integer.toString(this.cell_index));
		request.addField("cell_type", this.cell_type);
		request.sendMessage();
		
		try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				if(response_list.containsKey("card_add")) {
					MoveController.this.getLauncher().updateCard("add", response_list.get("card_add"));
				}
				
				if(response_list.containsKey("card_remove")) {
					MoveController.this.getLauncher().updateCard("remove", response_list.get("card_remove"));
				}
				
				if(response_list.containsKey("card_add") || response_list.containsKey("card_remove")) {
					MoveController.this.getLauncher().updateCards();
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
	
	public Launcher getLauncher() {
		return this.launcher;
	}
	
	public String getError() {
		return this.error_message;
	}
}
