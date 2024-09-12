package ex.exp1;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "pollID")
public class Poll {
    private Integer pollID;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private List<String> voteOptions = new ArrayList<>();

    @JsonProperty("creator")
    private User creator;

    public Poll() {
    }

    public Poll(Integer id, String question, Instant publishedAt, Instant validUntil, User user, List<String> voteOptions) {
        this.pollID = id;
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
        this.creator = user;
        this.voteOptions = voteOptions;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public void setVoteOptions(List<String> voteOptions) {
        this.voteOptions = voteOptions;
    }

    public void setCreator(User user) {
        this.creator = user;
    }

    public Integer getPollID() {
        return pollID;
    }

    public String getQuestion() {
        return question;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public User getCreator() {
        return creator;
    }

    public List<String> getVoteOptions() {
        return voteOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return Objects.equals(pollID, poll.getPollID()) &&
                Objects.equals(question, poll.question) &&
                Objects.equals(validUntil, poll.validUntil) &&
                Objects.equals(creator, poll.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollID, question, validUntil, creator);
    }
}