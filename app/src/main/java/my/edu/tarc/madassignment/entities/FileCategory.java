package my.edu.tarc.madassignment.entities;

import java.io.Serializable;

/**
 * Created by ASUS on 30/12/2017.
 */

public class FileCategory implements Serializable{
    private String subject_id;
    private String category_name;

    public FileCategory(String subject_id, String category_name) {
        this.subject_id = subject_id;
        this.category_name =category_name;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getFile_category() {
        return category_name;
    }

    public void setFile_category(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return category_name;
    }
}
