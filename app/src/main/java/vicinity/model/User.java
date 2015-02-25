package vicinity.model;

public class User {

    private String _username;


    /**
     * Public Constructor
     * @param username
     */
    public User(String username){
        this._username = username;
    }
    public User(){

    }

    //Setters & Getters
    public String getUsername() {
        return _username;
    }
    public void setUsername(String username) {
        this._username = username;
    }



}
