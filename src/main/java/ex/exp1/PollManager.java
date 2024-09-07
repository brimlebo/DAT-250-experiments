package ex.exp1;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class PollManager {
    private final Map<String, User> users = new HashMap<>();
    private final Map<Integer, Poll> polls = new HashMap<>();
    private final Map<Integer, Map<User, Vote>> pollVotes = new HashMap<>();

    public void addUser(User user) {
        users.putIfAbsent(user.getUsername(), user);
    }

    public Optional<User> getUser(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    public void addPoll(Poll poll) {
        polls.putIfAbsent(poll.getPollID(), poll);
        pollVotes.putIfAbsent(poll.getPollID(), new HashMap<>());
    }

    public Optional<Poll> getPoll(Integer pollID) {
        return Optional.ofNullable(polls.get(pollID));
    }

    public List<Poll> allPolls() {
        return new ArrayList<>(polls.values());
    }

    public String addOrUpdateUserVote(User user, Integer pollID, VoteOption voteOption) {
        Map<User, Vote> votes = pollVotes.getOrDefault(pollID, new HashMap<>());
        Vote vote = votes.get(user);
        if (vote != null) {
            vote.setSelectedOption(voteOption);
            vote.setPublishedAt(Instant.now());
            return "Vote has been updated";
        }
        else {
            String voteId = generateVoteId(pollID, user);
            Vote newVote = new Vote(voteId, Instant.now(), voteOption, user);
            votes.put(user, newVote);
            return "Vote has been created";
        }
    }

    // Method to generate a unique vote ID
    private String generateVoteId(Integer pollID, User user) {
        return pollID + "-" + user.getUsername() + "-" + UUID.randomUUID().toString();
    }

    public Optional<Vote> getVote(Integer pollID, String username) {
        Optional<User> user = getUser(username);
        return user.map(value -> pollVotes.get(pollID).get(value));
    }

    public List<Vote> listVotes(Integer pollID) {
        Map<User, Vote> userVotes = pollVotes.get(pollID);
        if (userVotes == null) return new ArrayList<>();
        return new ArrayList<>(userVotes.values());
    }

    public void removePoll(Integer pollID) {
        polls.remove(pollID);
        pollVotes.remove(pollID);
    }

    public void removeVote(Integer pollID, User user) {
        Map<User, Vote> userVotes = pollVotes.get(pollID);
        if (userVotes != null) userVotes.remove(user);
    }
}