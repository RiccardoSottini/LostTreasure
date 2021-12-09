package connection;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response<T> implements StompFrameHandler {
	private StompSession session;
	private String path;
	private HashMap<String, String> response;
	private T objectHandler;
	private AtomicBoolean status;
	
	private final int WAIT_TIME = 1000;
	
	public Response(StompSession session, String path) {
		this.session = session;
		this.path = path;
		this.response = new HashMap<String, String>();
		this.objectHandler = null;
		this.status = new AtomicBoolean(false);
		
		this.subscribe(this.path);
	}
	
	public Response(StompSession session, String path, T objectHandler) {
		this.session = session;
		this.path = path;
		this.response = new HashMap<String, String>();
		this.objectHandler = objectHandler;
		this.status = new AtomicBoolean(false);
	
		this.subscribe(this.path);
	}
	
	public void setSession(StompSession session) {
		this.session = session;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void subscribe(String path) {
		System.out.println("SUBSCRIBE: " + path);
		
		if(path != null && !path.equals("")) {
			this.session.subscribe(path, this);
		}
	}
	
	@Override
	public Type getPayloadType(StompHeaders headers) {
		return String.class;
	}

	@Override
	public synchronized void handleFrame(StompHeaders headers, Object payload) {
		try {
			this.response = new ObjectMapper().readValue((String) payload, HashMap.class);
			
			if(this.objectHandler != null) {
				Method method = this.objectHandler.getClass().getMethod("execute", HashMap.class);
				
				if(!method.isAccessible()) {
					method.setAccessible(true);	
					method.invoke(this.objectHandler, response);
				}
			} else {
				this.status.set(true);
				this.notifyAll();
			}
			
			System.out.println("\nResponse Message:");
			this.printFields();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
	}
	
	public synchronized HashMap<String, String> getResponse() throws Exception {
		long start_time = System.currentTimeMillis();
		
		while(!this.status.get() && (System.currentTimeMillis() - start_time) < WAIT_TIME) {
			try {
				this.wait(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(this.response.isEmpty()) {
			throw new Exception("ERROR: No response");
		}
		
		this.status.set(true);
		return this.response;
	}
	
	public void printFields() {
		for(String key : this.response.keySet()) {
			String value = this.response.get(key);
			
			System.out.println(key + ": " + value);
		}
	}
}
