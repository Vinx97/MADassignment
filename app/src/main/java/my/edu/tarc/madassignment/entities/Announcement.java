package my.edu.tarc.madassignment.entities;

import java.io.Serializable;

/**
 * Created by ASUS on 22/12/2017.
 */

public class Announcement implements Serializable{
    private String message;
    private String date;
    private String id;


    public Announcement(String id, String message, String date) {
        this.message = message;
        this.date = date;
        this.id = id;
    }

    public String getId(){ return id;}

    public void setId(){ this.id = id;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
