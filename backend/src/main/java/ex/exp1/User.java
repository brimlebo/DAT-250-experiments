package ex.exp1;

import java.util.Objects;

public class User {
    private Integer userID;
    private String username;
    private String email;

    public User() {}

    public User(Integer id, String username, String email) {
        this.userID = id;
        this.username = username;
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // Override equals() and hashCode() to compare field values
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
