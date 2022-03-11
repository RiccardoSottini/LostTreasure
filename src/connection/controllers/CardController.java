package connection.controllers;

import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class CardController {
	private final Launcher launcher;
	private final StompSession session;
	private final String user_token;
	private final String game_token;
	private final String card_value;
	
	private String error_message;
	
	public CardController(Launcher launcher, String user_token, String game_token, String card_value) {
		this.launcher = launcher;
		this.session = launcher.getSession();
		this.user_token = user_token;
		this.game_token = game_token;
		
		this.card_value = card_value;
	}
	
	public void sendSelect() {
		String sessionID = session.getSessionId();
		
		//Response response = new Response(session, "/response/game/card/select/" + sessionID);
		
		Request request = new Request(session, "/app/game/card/select/" + sessionID);
		request.addField("user_token", this.user_token);
		request.addField("game_token", this.game_token);
		request.addField("card_value", this.card_value);
		request.sendMessage();
		
		/*try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				return true;
			} else {
				this.error_message = response_list.get("error_message");
			}
		} catch (Exception e) {
			this.error_message = e.getMessage();
		}
		
		return false;*/
	}
	
	public void sendUnselect() {
		String sessionID = session.getSessionId();
		
		//Response response = new Response(session, "/response/game/card/unselect/" + sessionID);
		
		Request request = new Request(session, "/app/game/card/unselect/" + sessionID);
		request.addField("user_token", this.user_token);
		request.addField("game_token", this.game_token);
		request.addField("card_value", this.card_value);
		request.sendMessage();
		
		/*try {
			HashMap<String, String> response_list = response.getResponse();
			
			if(response_list.get("status").equals("success")) {
				return true;
			} else {
				this.error_message = response_list.get("error_message");
			}
		} catch (Exception e) {
			this.error_message = e.getMessage();
		}
		
		return false;*/
	}
	
	public Launcher getLauncher() {
		return this.launcher;
	}
	
	public String getError() {
		return this.error_message;
	}
}

