package facebook.socialnetwork.nhom3.facebook;

public class FriendsRequest {
    public String request_type;

    public FriendsRequest(String request_type) {
        this.request_type = request_type;
    }
    public FriendsRequest(){

    }
    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String date) {
        this.request_type = date;
    }
}
