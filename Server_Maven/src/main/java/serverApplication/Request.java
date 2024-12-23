package serverApplication;

import java.util.Map;

import Classes.User;

public class Request {
    private String action; 
    private User requestee;
    private Map<String, Object> data;

    public Request() {}

    public Request(String action, Map<String, Object> data,User requestee) {
        this.action = action;
        this.data = data;
        this.setRequestee(requestee);
    }

    public String getAction() {
        return action;
    }

    public Map<String, Object> getData() {
        return data;
    }

	public User getRequestee() {
		return requestee;
	}

	public void setRequestee(User requestee) {
		this.requestee = requestee;
	}
}
