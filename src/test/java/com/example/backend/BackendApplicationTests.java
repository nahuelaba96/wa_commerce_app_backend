package com.example.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BackendApplicationTests {
    private static final String INTERNAL_TOKEN = "estoes-pala";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("backend_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID authUserId;
    private long merchantId;

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("INTERNAL_SERVICE_TOKEN", () -> INTERNAL_TOKEN);
    }

    @BeforeEach
    void createOwnerThroughInternalOnboarding() throws Exception {
        authUserId = UUID.randomUUID();
        String body = """
                {"authUserId":"%s","email":"owner@example.com","personalName":"Owner","merchantName":"Test shop"}
                """.formatted(authUserId);
        JsonNode first = internalPost(body);
        JsonNode repeated = internalPost(body);
        assertEquals(first.path("userId").asLong(), repeated.path("userId").asLong());
        assertEquals(first.path("merchantId").asLong(), repeated.path("merchantId").asLong());
        merchantId = first.path("merchantId").asLong();
    }

    @Test
    void createsAndReadsOnlyResourcesInTheAuthenticatedMerchant() throws Exception {
        JsonNode category = post("/api/categories", """
                {"name":"Indumentaria","merchantId":%d}
                """.formatted(merchantId));
        long categoryId = category.path("id").asLong();
        JsonNode product = post("/api/products", """
                {
                  "name":"Camisa",
                  "description":"Camisa de prueba",
                  "brand":"Marca",
                  "merchantId":%d,
                  "categoryId":%d,
                  "variants":[
                    {"sku":"CAM-001","size":"M","color":"azul","price":25.50,"stock":4}
                  ]
                }
                """.formatted(merchantId, categoryId));
        long productId = product.path("id").asLong();

        post("/api/product-variants", """
                {
                  "sku":"CAM-002",
                  "size":"L",
                  "color":"negro",
                  "price":26.50,
                  "stock":3,
                  "productId":%d,
                  "merchantId":%d
                }
                """.formatted(productId, merchantId));
        assertEquals(1, get("/api/products?merchantId=" + merchantId).size());
        assertEquals(2, get("/api/product-variants?merchantId=" + merchantId).size());
        assertEquals(1, get("/api/categories?merchantId=" + merchantId).size());

        post("/api/product-images?merchantId=" + merchantId, """
                {"productId":%d}
                """.formatted(productId));
        assertEquals(1, get("/api/product-images?merchantId=" + merchantId).size());

        JsonNode customer = post("/api/customers", """
                {"name":"Test customer","phone":"+5491100000000","merchantId":%d}
                """.formatted(merchantId));
        JsonNode account = post("/api/whatsapp-accounts", """
                {"merchantId":%d}
                """.formatted(merchantId));
        JsonNode conversation = post("/api/conversations", """
                {"merchantId":%d,"customerId":%d,"whatsappAccountId":%d}
                """.formatted(merchantId, customer.path("id").asLong(), account.path("id").asLong()));
        post("/api/messages?merchantId=" + merchantId, """
                {"conversationId":%d}
                """.formatted(conversation.path("id").asLong()));

        assertEquals(1, get("/api/customers?merchantId=" + merchantId).size());
        assertEquals(1, get("/api/whatsapp-accounts?merchantId=" + merchantId).size());
        assertEquals(1, get("/api/conversations?merchantId=" + merchantId).size());
        assertEquals(1, get("/api/messages?merchantId=" + merchantId).size());
        assertEquals(1, get("/api/merchants/mine").size());
        assertEquals("Test shop", get("/api/merchants/" + merchantId).path("name").asText());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products")
                        .param("merchantId", Long.toString(merchantId + 1))
                        .with(jwt().jwt(token -> token.subject(authUserId.toString())
                                .claim("merchant_id", merchantId))))
                .andExpect(status().isForbidden());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\"}")
                        .with(jwt().jwt(token -> token.subject(authUserId.toString())
                                .claim("merchant_id", merchantId))))
                .andExpect(status().isForbidden());
        JsonNode deletableProduct = post("/api/products", """
                {"name":"Disposable","merchantId":%d,"categoryId":%d}
                """.formatted(merchantId, categoryId));
        mockMvc.perform(delete("/api/products").param("merchantId", Long.toString(merchantId))
                        .param("id", Long.toString(deletableProduct.path("id").asLong()))
                        .with(jwt().jwt(token -> token.subject(authUserId.toString())
                                .claim("merchant_id", merchantId))))
                .andExpect(status().isNoContent());
        assertEquals(1, get("/api/products?merchantId=" + merchantId).size());
    }

    @Test
    void internalOnboardingRequiresServiceCredentialAndMatchingIdempotencyKey() throws Exception {
        UUID requestId = UUID.randomUUID();
        String body = """
                {"authUserId":"%s","email":"internal@example.com","personalName":"Owner","merchantName":"Shop"}
                """.formatted(requestId);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/internal/auth/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Internal-Service-Token", "wrong")
                        .header("Idempotency-Key", requestId.toString())
                        .content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/internal/auth/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Internal-Service-Token", INTERNAL_TOKEN)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void corsAllowsConfiguredFrontendOriginToSendBearerTokens() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options("/api/products")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Headers", "authorization"));
    }

    private JsonNode internalPost(String body) throws Exception {
        MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/internal/auth/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Internal-Service-Token", INTERNAL_TOKEN)
                        .header("Idempotency-Key", authUserId.toString())
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode post(String path, String body) throws Exception {
        MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwt().jwt(token -> token.subject(authUserId.toString())
                                .claim("merchant_id", merchantId))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode get(String path) throws Exception {
        MvcResult result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(path)
                        .with(jwt().jwt(token -> token.subject(authUserId.toString())
                                .claim("merchant_id", merchantId))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
