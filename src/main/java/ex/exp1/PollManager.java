package ex.exp1;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PollManager {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Poll> polls = new HashMap<>();
    private final Map<String, Map<User, Vote>> pollVotes = new HashMap<>();

    public void addUser(User user) {
        users.putIfAbsent(user.getUsername(), user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    public void addPoll(User user, Poll poll) {
        polls.putIfAbsent(poll.getQuestion(), poll);
        pollVotes.putIfAbsent(poll.getQuestion(), new HashMap<>());
    }

    public Poll getPoll(String question) {
        return polls.get(question);
    }

    public List<Poll> allPolls() {
        return new ArrayList<>(polls.values());
    }

    public String addOrUpdateUserVote(User user, String pollQuestion, VoteOption voteOption) {
        Poll poll = getPoll(pollQuestion);
        if (poll == null) {
            return "Poll not found";
        }
        Map<User, Vote> userVotes = pollVotes.get(pollQuestion);
        Vote vote = userVotes.get(user);
        if (vote != null) {
            vote.setSelectedOption(voteOption);
            vote.setPublishedAt(Instant.now());
            userVotes.put(user, vote);
            return "Vote has been updated";
        }
        else {
            Vote newVote = new Vote(Instant.now(), voteOption, user);
            userVotes.put(user, newVote);
            return "Vote has been created";
        }
    }

    public List<Vote> listVotes(String question) {
        Map<User, Vote> userVotes = pollVotes.get(question);
        if (userVotes == null) return new ArrayList<>();
        return new ArrayList<>(userVotes.values());
    }

    public void removePoll(String question) {
        polls.remove(question);
        pollVotes.remove(question);
    }

    public void removeVote(String question, User user) {
        Map<User, Vote> userVotes = pollVotes.get(question);
        if (userVotes != null) userVotes.remove(user);
    }
}