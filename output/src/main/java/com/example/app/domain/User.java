package com.example.app.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String username;
    private String email;

public User() {}

// Getters and Setters
public Long getId() {
return id;
}

public void setId(Long id) {
this.id = id;
}

    public String getUsername() {
    return username;
    }

    public void setUsername(String username) {
    this.username = username;
    }
    public String getEmail() {
    return email;
    }

    public void setEmail(String email) {
    this.email = email;
    }



            @ManyToMany
            @JoinTable(
            name = "User_Post", // Name of the join table
            joinColumns = @JoinColumn(name = "User_id"), // FK for Student
            inverseJoinColumns = @JoinColumn(name = "Post_id") // FK for Course
            )
            private List<Post> posts = new ArrayList<>();





            @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
            private Post post;





        public List<Post> getPosts() {
        return posts;
        }

        public void setPosts(List<Post> posts) {
        this.posts = posts;
        }



        public Post getPost() {
        return post;
        }

        public void setPost(Post post) {
        this.post = post;
        }


}
