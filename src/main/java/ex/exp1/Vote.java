package ex.exp1;

import java.time.Instant;

public class Vote {
    private Instant publishedAt;
    private VoteOption selectedOption;
    private User user; // User who voted, for ease of tracking

    public Vote() {
    }

    public Vote(Instant publishedAt, VoteOption selectedOption, User user) {
        this.publishedAt = publishedAt;
        this.selectedOption = selectedOption;
        this.user = user;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setSelectedOption(VoteOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public VoteOption getSelectedOption() {
        return selectedOption;
    }

    public User getUser() {
        return user;
    }
}