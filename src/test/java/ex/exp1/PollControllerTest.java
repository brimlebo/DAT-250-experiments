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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User created"));

        // List all users
        mockMvc.perform(get("/pollApi/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"));

        // Create User 2
        mockMvc.perform(post("/pollApi/users")
                        .param("username", "user2")
                        .param("email", "user2@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User created"));

        // List all users again
        mockMvc.perform(get("/pollApi/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("user2"));

        // Create a poll
        Poll poll = new Poll(0, "Best color?", Instant.now(), Instant.now().plusSeconds(3600), new User(0, "user1", "user1@example.com"), List.of(
                new VoteOption("Red", 1, null),
                new VoteOption("Blue", 2, null)
        ));

        mockMvc.perform(post("/pollApi/polls")
                        .param("question", "Best color?")
                        .param("userID", "0")
                        .param("validUntil", poll.getValidUntil().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(poll.getVoteOptions())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Poll created"));

        // List all polls
        mockMvc.perform(get("/pollApi/polls"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].question").value("Best color?"));

        // User 2 votes on the poll
        VoteOption voteOption = new VoteOption("Red", 1, null);
        mockMvc.perform(post("/pollApi/polls/0/votes")
                        .param("userID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteOption)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Vote has been created"));

        // User 2 changes their vote
        VoteOption newVoteOption = new VoteOption("Blue", 2, null);
        mockMvc.perform(post("/pollApi/polls/0/votes")
                        .param("userID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVoteOption)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Vote has been updated"));

        // List votes, should return the changed vote (Blue not Red)
        mockMvc.perform(get("/pollApi/polls/0/votes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].selectedOption.caption").value("Blue"));

        // Delete the poll
        mockMvc.perform(delete("/pollApi/polls/0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Poll deleted"));

        // List votes again, should return an 404 as the poll no longer exists
        mockMvc.perform(get("/pollApi/polls/0/votes"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}