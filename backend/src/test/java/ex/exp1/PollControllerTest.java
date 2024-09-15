package ex.exp1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPollScenario() throws Exception {
        // Create User 1
        mockMvc.perform(post("/pollApi/users")
                        .param("username", "user1")
                        .param("email", "user1@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // List all users
        mockMvc.perform(get("/pollApi/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("user1"));

        // Create User 2
        mockMvc.perform(post("/pollApi/users")
                        .param("username", "user2")
                        .param("email", "user2@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // List all users again
        mockMvc.perform(get("/pollApi/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].username").value("user2"));

        // Create a poll
        Poll poll = new Poll(1, "Best color?", Instant.now(), Instant.now().plusSeconds(3600), new User(2, "user1", "user1@example.com"), List.of(
                "Red",
                "Blue"
        ));

        PollCreationRequest req = new PollCreationRequest(poll.getQuestion(), 2, poll.getValidUntil(), poll.getVoteOptions());

        mockMvc.perform(post("/pollApi/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // List all polls
        mockMvc.perform(get("/pollApi/polls"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].question").value("Best color?"));

        // User 2 votes on the poll
        String voteOption = "Red";
        mockMvc.perform(post("/pollApi/polls/1/votes")
                        .param("userID", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteOption))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // User 2 changes their vote
        String newVoteOption = "Blue";
        mockMvc.perform(post("/pollApi/polls/1/votes")
                        .param("userID", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newVoteOption))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Vote has been updated"));

        // List votes, should return the changed vote (Blue not Red)
        mockMvc.perform(get("/pollApi/polls/1/votes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].selectedOption").value("Blue"));

        // Delete the poll
        mockMvc.perform(delete("/pollApi/polls/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Poll deleted"));

        // List votes again, should return an 404 as the poll no longer exists
        mockMvc.perform(get("/pollApi/polls/1/votes"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}