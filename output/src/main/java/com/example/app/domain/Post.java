package com.example.app.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Post {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String title;
    private String content;

public Post() {}

// Getters and Setters
public Long getId() {
return id;
}

public void setId(Long id) {
this.id = id;
}

    public String getTitle() {
    return title;
    }

    public void setTitle(String title) {
    this.title = title;
    }
    public String getContent() {
    return content;
    }

    public void setContent(String content) {
    this.content = content;
    }





        // Inverse side of bidirectional relationship

            private List<User> users = new ArrayList<>();





        // Inverse side of bidirectional relationship


            @OneToOne
            @JoinColumn(name = "id_author")
            private User user;




        public List<User> getUsers() {
        return users;
        }

        public void setUsers(List<User> users) {
        this.users = users;
        }




        public User getUser() {
        return user;
        }

        public void setUser(User user) {
        this.user = user;
        }



}
