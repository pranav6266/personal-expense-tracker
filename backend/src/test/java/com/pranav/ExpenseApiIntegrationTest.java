package com.pranav;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"postgres-db", "test"})
class ExpenseApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void expensesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCanSignUpLogInAndManageOwnExpenses() throws Exception {
        String token = signUpAndLogIn("alice");

        mockMvc.perform(post("/expenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"expenseType": 0, "date": "2026-09-01", "amount": 250.5,
                                 "category": "Groceries", "account": "Cash", "note": "Weekly shop"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.category").value("Groceries"));

        mockMvc.perform(get("/expenses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(250.5));

        mockMvc.perform(get("/auth/validate").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void usersCannotSeeEachOthersExpenses() throws Exception {
        String bob = signUpAndLogIn("bob");
        String carol = signUpAndLogIn("carol");

        mockMvc.perform(post("/expenses")
                        .header("Authorization", "Bearer " + bob)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"expenseType": 0, "date": "2026-09-02", "amount": 40,
                                 "category": "Transport", "account": "Card", "note": "Metro"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/expenses").header("Authorization", "Bearer " + carol))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private String signUpAndLogIn(String username) throws Exception {
        String credentials = """
                {"fullName": "%s Test", "username": "%s", "password": "s3cret-pass"}
                """.formatted(username, username);

        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isOk());

        String body = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "%s", "password": "s3cret-pass"}
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        return json.get("token").asText();
    }
}
