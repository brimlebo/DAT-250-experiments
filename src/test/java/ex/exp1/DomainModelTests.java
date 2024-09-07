package ex.exp1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DomainModelTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    public void testScenario() {
        User user1 = new User("User1", "user1@mail.com");
        User user2 = new User("User2", "user2@mail.com");
        String encodedQuestion = URLEncoder.encode("Do you like pizza?", StandardCharsets.UTF_8);
        Poll poll = new Poll(0, "Do you like pizza?", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), user1, new ArrayList<VoteOption>());
        poll.setVoteOptions(List.of(new VoteOption("Yes", 1, poll), new VoteOption("No (I'm lying to myself)", 2, poll)));
        HttpHeaders headers = new HttpHeaders();

        // Make user1
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/pollApi/users?username=User1&email=user1@mail.com", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User created");

        // Check that user1 exists
        ResponseEntity<User[]> usersResponse = restTemplate.getForEntity(baseUrl + "/pollApi/users", User[].class);
        assertThat(usersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<User> users = List.of(Objects.requireNonNull(usersResponse.getBody()));
        assertThat(users).contains(user1);

        // Make user2
        response = restTemplate.postForEntity(baseUrl + "/pollApi/users?username=User2&email=user2@mail.com", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("User created");

        // Check that both users exist
        usersResponse = restTemplate.getForEntity(baseUrl + "/pollApi/users", User[].class);
        assertThat(usersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        users = List.of(Objects.requireNonNull(usersResponse.getBody()));
        assertThat(users).contains(user1, user2);

        // User1 creates a poll
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<List<VoteOption>> request = new HttpEntity<>(poll.getVoteOptions(), headers);
        String pollUrl = baseUrl + "/pollApi/polls?question=" + encodedQuestion + "&username=" + user1.getUsername() + "&validUntil=" + poll.getValidUntil();
        response = restTemplate.exchange(pollUrl, HttpMethod.POST, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Poll created");

        // Check that poll exists
        ResponseEntity<Poll[]> pollsResponse = restTemplate.getForEntity(baseUrl + "/pollApi/polls", Poll[].class);
        assertThat(pollsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Poll> polls = List.of(Objects.requireNonNull(pollsResponse.getBody()));
        Poll outPoll = polls.getFirst();
        String decodedQuestion = URLDecoder.decode(outPoll.getQuestion(), StandardCharsets.UTF_8);
        assertThat(decodedQuestion).isEqualTo(poll.getQuestion());
        assertThat(outPoll.getValidUntil()).isEqualTo(poll.getValidUntil());
        assertThat(outPoll.getCreator()).isEqualTo(poll.getCreator());
        /*assertThat(polls).contains(poll);*/

        /*// User2 makes a vote
        VoteOption voteOption = poll.getVoteOptions().getFirst();
        HttpEntity<VoteOption> requestVote = new HttpEntity<>(voteOption, headers);
        String encodedQuestion = URLEncoder.encode(poll.getQuestion(), StandardCharsets.UTF_8);
        String url = String.format("%s/pollApi/polls/%s/votes?username=user2", baseUrl, encodedQuestion);
        response = restTemplate.exchange(url, HttpMethod.POST, requestVote, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Vote has been created");

        // User2 changes the vote
        voteOption = poll.getVoteOptions().getLast();
        requestVote = new HttpEntity<>(voteOption, headers);
        response = restTemplate.exchange(url, HttpMethod.POST, requestVote, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Vote has been updated");

        // check user2's votes, expect only updated vote (somewhat muddled as instants may not be the same so only comparing
        ResponseEntity<Vote> voteResponse = restTemplate.getForEntity(baseUrl + "/pollApi/polls/Do%you%like%pizza?/votes/user2", Vote.class);
        assertThat(voteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Vote vote = voteResponse.getBody();
        assertThat(vote).isNotNull();
        assertThat(vote.getSelectedOption().getCaption()).isEqualTo("No (I'm lying to myself)");
        assertThat(vote.getSelectedOption().getPresentationOrder()).isEqualTo(2);*/

        // Delete the poll
        String deleteUrl = baseUrl + "/pollApi/polls/" + encodedQuestion;
        response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, String.class );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Poll deleted");

        // Check if any votes exist, expect no votes
        ResponseEntity<Vote[]> votesResponse = restTemplate.getForEntity(baseUrl + "/pollApi/polls/Do%20you%20like%20pizza?/votes", Vote[].class);
        assertThat(votesResponse.getStatusCode()).isEqualTo(400);
        assertThat(votesResponse.getBody()).isEmpty();
    }
}
