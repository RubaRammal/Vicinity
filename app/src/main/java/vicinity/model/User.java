package vicinity.model;

public class User {

    protected String _username;
    protected boolean status;

    /**
     * Public Constructor initializes username, and sets status to be true
     * assuming that when any user (either a neighbor or a friend) added to the
     * app for the first time, the user shall be online.
     * @param username user's username
     */
    public User(String username){
        this._username = username;
        status=true;
    }
    public User(){
    }

    /**
     * setters/getters
     */
    public String getUsername() {
        return _username;
    }
    public void setUsername(String username) {
        this._username = username;
    }
    public boolean isOnline(){return status;}
    public void setStatus(boolean status){this.status=status;}



}
