package ex.exp1;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "user")
public class Vote {
    // ID currently not used for much, trying to figure out how to implement properly
    private String voteID;
    private Instant publishedAt;

    @JsonManagedReference
    private String selectedOption;

    @JsonIdentityReference(alwaysAsId = true)
    private User user; // User who voted, for ease of tracking

    public Vote() {
    }

    public Vote(String id, Instant publishedAt, String selectedOption, User user) {
        this.voteID = id;
        this.publishedAt = publishedAt;
        this.selectedOption = selectedOption;
        this.user = user;
    }

    public void setId(String id) {
        this.voteID = id;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVoteId() {
        return voteID;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(selectedOption, vote.selectedOption) &&
                Objects.equals(user, vote.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedOption, user);
    }
}