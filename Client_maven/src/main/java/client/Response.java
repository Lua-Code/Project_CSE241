package client;

import java.util.Map;


public class Response {
    private String status; 
    private Map<String, Object> data; 

    public Response() {}
    public Response(String action, Map<String, Object> data) {
        this.status = action;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }

}
