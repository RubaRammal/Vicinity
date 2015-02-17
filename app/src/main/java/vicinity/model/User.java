package vicinity.model;


public abstract class User {
    private static final String TAG = "UserClass";

    //User Atts
    private String _username;

    //Constructor
    public User(String username){
        this._username = username;
    }

    //Setters & Getters
    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        this._username = username;
    }

}
