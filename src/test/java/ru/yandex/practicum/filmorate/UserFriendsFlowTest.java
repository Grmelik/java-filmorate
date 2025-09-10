package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class UserFriendsFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private String createUser(String email, String login) throws Exception {
        String payload = "{" +
                "\"email\":\"" + email + "\"," +
                "\"login\":\"" + login + "\"," +
                "\"name\":\"User\"," +
                "\"birthday\":\"1990-01-01\"" +
                "}";

        return mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String extractId(String json) {
        int idx = json.indexOf("\"id\":");
        if (idx < 0) return null;
        int start = json.indexOf(':', idx) + 1;
        int end = json.indexOf(',', start);
        if (end < 0) end = json.indexOf('}', start);
        return json.substring(start, end).trim();
    }

    @Test
    @DisplayName("friends add/list/common/delete flow")
    void friendsFlow_ok() throws Exception {
        String u1 = createUser("f1@example.com", "f1");
        String u2 = createUser("f2@example.com", "f2");
        String u3 = createUser("f3@example.com", "f3");

        String id1 = extractId(u1);
        String id2 = extractId(u2);
        String id3 = extractId(u3);

        mockMvc.perform(put("/users/" + id1 + "/friends/" + id2))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/" + id1 + "/friends/" + id3))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/" + id2 + "/friends/" + id3))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + id1 + "/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(get("/users/" + id1 + "/friends/common/" + id2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Integer.parseInt(id3)));

        mockMvc.perform(delete("/users/" + id1 + "/friends/" + id3))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/" + id1 + "/friends"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("add friend for non-existing user returns 404")
    void addFriend_nonExistingUser_notFound() throws Exception {
        String u1 = createUser("nf@example.com", "nf");
        String id1 = extractId(u1);
        mockMvc.perform(put("/users/999999/friends/" + id1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("get friends common for non-existing users returns 404")
    void common_nonExistingUsers_notFound() throws Exception {
        mockMvc.perform(get("/users/999998/friends/common/999999"))
                .andExpect(status().isNotFound());
    }
}


