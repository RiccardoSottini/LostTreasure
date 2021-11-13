package connection;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class ConnectionSessionHandler implements StompSessionHandler {

	@Override
	public Type getPayloadType(StompHeaders headers) {
		 return String.class;
	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		session.send("/app/game/create", "test");

		session.subscribe("/response/game/create", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
            	ObjectMapper mapper = new ObjectMapper();
            	
				try {
					GameModel model = mapper.readValue((String) payload, GameModel.class);
	            	
	            	System.out.println(model.token);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
            }

        });
		
	    System.out.println("Connected to the Server!");
	}
	
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Message msg = (Message) payload;
		
	    System.out.println(msg);
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		System.out.println(exception);

	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		System.out.println("error");
	}

}
