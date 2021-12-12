package connection.controllers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.Request;
import connection.Response;
import game.Launcher;

public class UpdateController {
	private final Launcher launcher;
	private final StompSession session;
	private final String user_token;
	
	private String error_message;
	
	public UpdateController(Launcher launcher, StompSession session, String user_token) {
		this.launcher = launcher;
		this.session = session;
		this.user_token = user_token;
		
		this.retrieveUpdate();
	}
	
	public void retrieveUpdate() {
		new Response(session, "/response/game/update/" + user_token, new Object() {
			public synchronized void execute(HashMap<String, String> response_list) {
				boolean check_finished = UpdateController.this.getLauncher().isFinished();
				
				if(!check_finished) {
					if(response_list.get("method").equals("turn")) {
						int user_index = Integer.parseInt(response_list.get("turn_index"));
						UpdateController.this.getLauncher().updateTurn(user_index);
					} else if(response_list.get("method").equals("dice")) {
						int dice = Integer.parseInt(response_list.get("dice"));
						int turn_index = Integer.parseInt(response_list.get("turn_index"));
						boolean can_move = Boolean.parseBoolean(response_list.get("can_move"));
						UpdateController.this.getLauncher().updateDices(dice, turn_index, can_move);
					} else if(response_list.get("method").equals("move")) {
						int turn_index = Integer.parseInt(response_list.get("turn_index"));
						int archeologist_index = Integer.parseInt(response_list.get("archeologist_index"));
						int cell_index = Integer.parseInt(response_list.get("cell_index"));
						String cell_type = response_list.get("cell_type");
						UpdateController.this.getLauncher().updateArcheologist(turn_index, archeologist_index, cell_index, cell_type);
					
						if(response_list.get("kill_status").equals("true")) {
							int kill_user_index = Integer.parseInt(response_list.get("kill_user_index"));
							int kill_archeologist_index = Integer.parseInt(response_list.get("kill_archeologist_index"));
							
							UpdateController.this.getLauncher().updateKill(kill_user_index, kill_archeologist_index);
						}
						
						if(response_list.get("user_won").equals("true")) {
							int won_index = Integer.parseInt(response_list.get("won_index"));
							boolean is_finished = Boolean.parseBoolean(response_list.get("is_finished"));
							
							UpdateController.this.getLauncher().updateWon(turn_index, won_index, is_finished);
						}
					}
				}
				
				if(response_list.get("method").equals("message")) {
					int user_index = Integer.parseInt(response_list.get("user_index"));
					String user_name = response_list.get("user_name");
					String user_message = response_list.get("user_message");
					
					UpdateController.this.getLauncher().updateMessage(user_index, user_name, user_message);
				}
			}
		});
	}
	
	public Launcher getLauncher() {
		return this.launcher;
	}
	
	public String getError() {
		return this.error_message;
	}
}
