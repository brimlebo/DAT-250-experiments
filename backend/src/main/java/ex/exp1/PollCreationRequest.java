package ex.exp1;

import java.time.Instant;
import java.util.List;

// A small record class to handle the input for the creation of new polls
public record PollCreationRequest(String question, Integer userID, Instant validUntil, List<String> voteOptions) {
}
