package ex.exp1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pollApi/")
public class PollController {

    @Autowired
    private PollManager pollManager;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestParam String username, @RequestParam String email) {
        Integer userID = pollManager.allUsers().size();
        User user = new User(userID, username, email);
        pollManager.addUser(user);
        return ResponseEntity.ok("User created");
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
    public ResponseEntity<String> addPoll(@RequestParam String question, @RequestParam Integer userID, @RequestParam Instant validUntil, @RequestBody List<String> voteOptions) {
        Optional<User> user = pollManager.getUser(userID);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Integer pollID = pollManager.allPolls().size();
        Instant publishedAt = Instant.now();
        String decodedQuestion = URLDecoder.decode(question, StandardCharsets.UTF_8);
        Poll poll = new Poll(pollID, decodedQuestion, publishedAt, validUntil, user.get(), voteOptions);
        pollManager.addPoll(poll);
        return ResponseEntity.ok("Poll created");
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
