package ex.exp1;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/pollApi/")
public class PollController {

    @Autowired
    private PollManager pollManager;

    // Some test data for the front-end
    @PostConstruct
    public void createInitialState() {
        createUser("a", "a@test.com");
        createUser("b", "b@test.com");
        addPoll(new PollCreationRequest("Best food?", 0, Instant.now().plusSeconds(3600), List.of("Pizza", "Crayons")));
    }

    // Currently no checks on if a username or mail already exists so there can be multiple with the same
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestParam String username, @RequestParam String email) {
        Integer userID = pollManager.allUsers().size();
        User user = new User(userID, username, email);
        pollManager.addUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(pollManager.allUsers());
    }

    @GetMapping("/users/{userID}")
    public ResponseEntity<User> getUser(@PathVariable Integer userID) {
        Optional<User> user = pollManager.getUser(userID);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Add pole
    @PostMapping("/polls")
    public ResponseEntity<Poll> addPoll(@RequestBody PollCreationRequest request) {
        Optional<User> user = pollManager.getUser(request.userID());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Integer pollID = pollManager.allPolls().size();
        Instant publishedAt = Instant.now();
        Poll poll = new Poll(pollID, request.question(), publishedAt, request.validUntil(), user.get(), request.voteOptions());
        pollManager.addPoll(poll);
        return ResponseEntity.ok(poll);
    }

    // Find all poles
    @GetMapping("/polls")
    public ResponseEntity<List<Poll>> getPolls() {
        return ResponseEntity.ok(pollManager.allPolls());
    }

    @GetMapping("/polls/{pollID}")
    public ResponseEntity<Poll> getPoll(@PathVariable Integer pollID) {
        Optional<Poll> poll = pollManager.getPoll(pollID);

        return poll.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/polls/{pollID}")
    public ResponseEntity<String> deletePoll(@PathVariable Integer pollID) {
        Optional<Poll> poll = pollManager.getPoll(pollID);

        if (poll.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Poll not found");
        }

        pollManager.removePoll(pollID);
        return ResponseEntity.ok("Poll deleted");
    }

    @PostMapping("/polls/{pollID}/votes")
    public ResponseEntity<String> vote(@PathVariable Integer pollID, @RequestBody String voteOption, @RequestParam Integer userID) {
        Optional<User> user = pollManager.getUser(userID);
        Optional<Poll> poll = pollManager.getPoll(pollID);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        else if (poll.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Poll not found");
        }

        String response = pollManager.addOrUpdateUserVote(user.get(), pollID, voteOption);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/polls/{pollID}/votes")
    public ResponseEntity<List<Vote>> getVotes(@PathVariable Integer pollID) {
        Optional<Poll> poll = pollManager.getPoll(pollID);

        if (poll.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(pollManager.listVotes(pollID));
    }

    // Returns vote count for each option in a poll
    @GetMapping("/polls/{pollID}/voteCounts")
    public ResponseEntity<Map<String, Integer>> getVoteCounts(@PathVariable Integer pollID) {
        Optional<Poll> poll = pollManager.getPoll(pollID);

        if (poll.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Map<String, Integer> voteCounts = new HashMap<>();
        List<Vote> votes = pollManager.listVotes(pollID);

        // Count votes for each option
        for (Vote vote : votes) {
            String option = vote.getSelectedOption();
            voteCounts.put(option, voteCounts.getOrDefault(option, 0) + 1);
        }

        return ResponseEntity.ok(voteCounts);
    }

    @GetMapping("/polls/{pollID}/votes/{userID}")
    public ResponseEntity<Vote> getVote(@PathVariable Integer pollID, @PathVariable Integer userID) {
        Optional<Vote> vote = pollManager.getVote(pollID, userID);

        return vote.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/polls/{pollID}/votes")
    public ResponseEntity<String> deleteVote(@PathVariable Integer pollID, @RequestParam Integer userID) {
        Optional<User> user = pollManager.getUser(userID);
        Optional<Poll> poll = pollManager.getPoll(pollID);

        if (user.isEmpty()) {
            return ResponseEntity.ok("User not found");
        }
        else if (poll.isEmpty()) {
            return ResponseEntity.ok("Poll not found");
        }

        pollManager.removeVote(pollID, user.get());
        return ResponseEntity.ok("Vote deleted");
    }
}
