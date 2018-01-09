package my.edu.tarc.madassignment.entities;

/**
 * Created by User on 3/1/2018.
 */

public class loginPage {
    private String id;
    private String email;
    private String Password;
    private String type;

    public loginPage(){

    }

    public loginPage(String id, String email, String Password, String type) {
        this.id = id;
        this.email = email;
        this.Password = Password;
        this.type = type;
    }
    public void setId(String id){this.id = id;}

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
        return "login details :- /n"+
                "Email : "+email+"/n"+
                "Password : "+Password+"/n"+
                "type : "+type+"/n";
    }
}
