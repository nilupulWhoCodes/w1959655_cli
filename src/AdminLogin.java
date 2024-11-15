
public class AdminLogin implements Login {

    protected String username;
    protected String password;

    public AdminLogin (String username,String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean loginUser(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
}
