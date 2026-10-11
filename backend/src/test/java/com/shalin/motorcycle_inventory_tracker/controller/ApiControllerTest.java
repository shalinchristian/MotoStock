package com.shalin.motorcycle_inventory_tracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Full-stack controller tests against the seeded H2 dev database (security + validation + JSON shape). */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    String adminToken;
    String staffToken;

    @BeforeEach
    void login() throws Exception {
        adminToken = token("admin", "admin123");
        staffToken = token("staff", "staff123");
    }

    String token(String u, String p) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"" + p + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(body).get("token").asText();
    }

    static final String NEW_BIKE = """
            {"brand":"Triumph","model":"Speed 400","manufacturingYear":2024,"price":233000,"color":"Red",
             "engineCapacityCc":398,"stockQuantity":3,"category":"STANDARD","photoUrl":"/img/standard.svg"}""";

    @Test
    void catalogIsPublicAndFilterable() throws Exception {
        mvc.perform(get("/api/motorcycles")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(10)));
        mvc.perform(get("/api/motorcycles").param("brand", "royal enfield")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/motorcycles").param("category", "SPORT").param("sort", "price_asc"))
                .andExpect(jsonPath("$[0].model").value("Pulsar NS200"));
        mvc.perform(get("/api/motorcycles").param("q", "duke")).andExpect(jsonPath("$[0].brand").value("KTM"));
        mvc.perform(get("/api/motorcycles/low-stock"))
                .andExpect(jsonPath("$[*].model", hasItems("R15 V4", "390 Duke", "Apache RTR 310")));
    }

    @Test
    void writesRequireLogin() throws Exception {
        mvc.perform(post("/api/motorcycles").contentType(MediaType.APPLICATION_JSON).content(NEW_BIKE))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/dashboard").header("Authorization", "Bearer not-a-token")).andExpect(status().isUnauthorized());
    }

    @Test
    void badLoginIs401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"wrong\"}")).andExpect(status().isUnauthorized());
    }

    @Test
    void createUpdateAndValidate() throws Exception {
        String created = mvc.perform(post("/api/motorcycles").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_BIKE))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();

        mvc.perform(put("/api/motorcycles/" + id).header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_BIKE.replace("\"Red\"", "\"Black\"")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.color").value("Black"));

        mvc.perform(post("/api/motorcycles").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"brand\":\"\",\"price\":-1,\"engineCapacityCc\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.brand").value("Brand is required"))
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"))
                .andExpect(jsonPath("$.engineCapacityCc").value("Engine capacity must be at least 50 cc"));

        mvc.perform(get("/api/motorcycles/9999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Motorcycle not found with id: 9999"));
    }

    @Test
    void electricBikesUseKwInsteadOfCc() throws Exception {
        mvc.perform(get("/api/motorcycles").param("q", "450X"))
                .andExpect(jsonPath("$[0].engineCapacityCc").doesNotExist())
                .andExpect(jsonPath("$[0].motorPowerKw").value(6.4));

        String ev = """
                {"brand":"Ola","model":"S1 Pro","manufacturingYear":2024,"price":130000,"color":"White",
                 "stockQuantity":4,"category":"ELECTRIC","motorPowerKw":11}""";
        mvc.perform(post("/api/motorcycles").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(ev))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.motorPowerKw").value(11));

        mvc.perform(post("/api/motorcycles").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(ev.replace(",\"motorPowerKw\":11", "")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.motorPowerKwPresent").exists());

        mvc.perform(post("/api/motorcycles").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_BIKE.replace("\"engineCapacityCc\":398,", "")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.engineCapacityCcPresent").exists());
    }

    @Test
    void deleteIsAdminOnly() throws Exception {
        String created = mvc.perform(post("/api/motorcycles").header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(NEW_BIKE)).andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();
        mvc.perform(delete("/api/motorcycles/" + id).header("Authorization", staffToken)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/motorcycles/" + id).header("Authorization", adminToken)).andExpect(status().isNoContent());
    }

    @Test
    void saleReducesStockAndShowsOnDashboard() throws Exception {
        JsonNode bike = json.readTree(mvc.perform(get("/api/motorcycles").param("q", "Classic"))
                .andReturn().getResponse().getContentAsString()).get(0);
        long id = bike.get("id").asLong();
        int stock = bike.get("stockQuantity").asInt();

        mvc.perform(post("/api/sales").header("Authorization", staffToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motorcycleId\":" + id + ",\"quantity\":2,\"customerName\":\"Test Buyer\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.soldBy").value("staff"));
        mvc.perform(get("/api/motorcycles/" + id)).andExpect(jsonPath("$.stockQuantity").value(stock - 2));

        mvc.perform(post("/api/sales").header("Authorization", staffToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motorcycleId\":" + id + ",\"quantity\":999,\"customerName\":\"Too Many\"}"))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/dashboard").header("Authorization", staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalModels").value(10))
                .andExpect(jsonPath("$.salesByMonth", hasSize(6)))
                .andExpect(jsonPath("$.lowStockCount", greaterThanOrEqualTo(3)));
    }

    @Test
    void testRideBookingAndCalendar() throws Exception {
        LocalDateTime slot = LocalDateTime.now().plusDays(20).withHour(15).withMinute(0).withSecond(0).withNano(0);
        String body = "{\"motorcycleId\":1,\"customerName\":\"Rider\",\"customerPhone\":\"9825011111\",\"scheduledAt\":\"" + slot + "\"}";
        String created = mvc.perform(post("/api/test-rides").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andReturn().getResponse().getContentAsString();
        mvc.perform(post("/api/test-rides").header("Authorization", staffToken)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());

        mvc.perform(get("/api/test-rides").header("Authorization", staffToken)
                        .param("from", slot.toLocalDate().toString()).param("to", slot.toLocalDate().toString()))
                .andExpect(jsonPath("$", hasSize(1)));

        long id = json.readTree(created).get("id").asLong();
        mvc.perform(patch("/api/test-rides/" + id + "/status").header("Authorization", staffToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"COMPLETED\"}"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void openApiIsServed() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.info.title").value("MotoStock API"));
    }
}
