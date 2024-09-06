package ex.exp1;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Poll {
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private User creator;
    private List<VoteOption> voteOptions = new ArrayList<>();

    public Poll() {
    }

    public Poll(String question, Instant publishedAt, Instant validUntil, User user, List<VoteOption> voteOptions) {
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

    public void setVoteOptions(List<VoteOption> voteOptions) {
        this.voteOptions = voteOptions;
    }

    public void setCreator(User user) {
        this.creator = user;
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

    public List<VoteOption> getVoteOptions() {
        return voteOptions;
    }
}