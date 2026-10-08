package com.example.backend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import com.example.backend.dto.CreateCategoryRequest;
import com.example.backend.dto.CreateConversationRequest;
import com.example.backend.dto.CreateCustomerRequest;
import com.example.backend.dto.CreateMerchantRequest;
import com.example.backend.dto.CreateMerchantUserRequest;
import com.example.backend.dto.CreateMessageRequest;
import com.example.backend.dto.CreateProductImageRequest;
import com.example.backend.dto.CreateProductRequest;
import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.CreateUserRequest;
import com.example.backend.dto.CreateWhatsAppAccountRequest;
import com.example.backend.controller.CategoryController;
import com.example.backend.controller.ConversationController;
import com.example.backend.controller.CustomerController;
import com.example.backend.controller.MerchantController;
import com.example.backend.controller.MerchantUserController;
import com.example.backend.controller.MessageController;
import com.example.backend.controller.ProductController;
import com.example.backend.controller.ProductImageController;
import com.example.backend.controller.ProductVariantController;
import com.example.backend.controller.UserController;
import com.example.backend.controller.WhatsAppAccountController;
import com.example.backend.service.CategoryService;
import com.example.backend.service.ConversationService;
import com.example.backend.service.CustomerService;
import com.example.backend.service.MerchantService;
import com.example.backend.service.MerchantUserService;
import com.example.backend.service.MessageService;
import com.example.backend.service.ProductImageService;
import com.example.backend.service.ProductService;
import com.example.backend.service.ProductVariantService;
import com.example.backend.service.UserService;
import com.example.backend.service.WhatsAppAccountService;

class BackendEndpointsTest {
    private final CategoryService categoryService = mock(CategoryService.class);
    private final ConversationService conversationService = mock(ConversationService.class);
    private final CustomerService customerService = mock(CustomerService.class);
    private final MerchantService merchantService = mock(MerchantService.class);
    private final MerchantUserService merchantUserService = mock(MerchantUserService.class);
    private final MessageService messageService = mock(MessageService.class);
    private final ProductService productService = mock(ProductService.class);
    private final ProductImageService productImageService = mock(ProductImageService.class);
    private final ProductVariantService productVariantService = mock(ProductVariantService.class);
    private final UserService userService = mock(UserService.class);
    private final WhatsAppAccountService whatsAppAccountService = mock(WhatsAppAccountService.class);

    private MockMvc categories;
    private MockMvc conversations;
    private MockMvc customers;
    private MockMvc merchants;
    private MockMvc merchantUsers;
    private MockMvc messages;
    private MockMvc products;
    private MockMvc productImages;
    private MockMvc productVariants;
    private MockMvc users;
    private MockMvc whatsAppAccounts;

    @BeforeEach
    void setUp() {
        categories = mvc(new CategoryController(categoryService));
        conversations = mvc(new ConversationController(conversationService));
        customers = mvc(new CustomerController(customerService));
        merchants = mvc(new MerchantController(merchantService));
        merchantUsers = mvc(new MerchantUserController(merchantUserService));
        messages = mvc(new MessageController(messageService));
        products = mvc(new ProductController(productService));
        productImages = mvc(new ProductImageController(productImageService));
        productVariants = mvc(new ProductVariantController(productVariantService));
        users = mvc(new UserController(userService));
        whatsAppAccounts = mvc(new WhatsAppAccountController(whatsAppAccountService));
    }

    @Test
    void getsResourcesForMerchantAcrossAllMerchantScopedEndpoints() throws Exception {
        categories.perform(get("/api/categories").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        conversations.perform(get("/api/conversations").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        customers.perform(get("/api/customers").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        messages.perform(get("/api/messages").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        products.perform(get("/api/products").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        productImages.perform(get("/api/product-images").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        productVariants.perform(get("/api/product-variants").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        users.perform(get("/api/users").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        whatsAppAccounts.perform(get("/api/whatsapp-accounts").param("merchantId", "20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));

        verify(categoryService).getCategories(20L);
        verify(conversationService).getConversations(20L);
        verify(customerService).getCustomers(20L);
        verify(messageService).getMessages(20L);
        verify(productService).getProducts(20L);
        verify(productImageService).getProductImages(20L);
        verify(productVariantService).getProductVariants(20L);
        verify(userService).getUsers(20L);
        verify(whatsAppAccountService).getWhatsAppAccounts(20L);
    }

    @Test
    void getsMerchantAndMerchantMemberships() throws Exception {
        merchants.perform(get("/api/merchants/20")).andExpect(status().isOk());
        merchantUsers.perform(get("/api/merchant-users/merchant/20"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));

        verify(merchantService).getMerchant(20L);
        verify(merchantUserService).getMemberships(20L);
    }

    @Test
    void createsResourcesAcrossAllPostEndpoints() throws Exception {
        postJson(merchants, "/api/merchants", """
                {"name":"Tienda","ownerUserId":30}
                """);
        postJson(merchantUsers, "/api/merchant-users", """
                {"merchantId":20,"userId":30,"role":"ADMIN"}
                """);
        postJson(users, "/api/users", """
                {"email":"user@example.com"}
                """);
        postJson(categories, "/api/categories", """
                {"name":"Ropa","merchantId":20}
                """);
        postJson(products, "/api/products", """
                {"name":"Camisa","merchantId":20,"categoryId":30}
                """);
        postJson(productImages, "/api/product-images", """
                {"productId":40}
                """, "merchantId", "20");
        postJson(productVariants, "/api/product-variants", """
                {"sku":"CAM-01","price":10.00,"stock":2,"productId":40,"merchantId":20}
                """);
        postJson(customers, "/api/customers", """
                {"phone":"+541112345678","merchantId":20}
                """);
        postJson(conversations, "/api/conversations", """
                {"merchantId":20,"customerId":50}
                """);
        postJson(messages, "/api/messages", """
                {"conversationId":60}
                """, "merchantId", "20");
        postJson(whatsAppAccounts, "/api/whatsapp-accounts", """
                {"merchantId":20}
                """);

        verify(merchantService).createMerchant(new CreateMerchantRequest("Tienda", 30L));
        verify(merchantUserService).addUser(new CreateMerchantUserRequest(20L, 30L, "ADMIN"));
        verify(userService).createUser(new CreateUserRequest("user@example.com"));
        verify(categoryService).createCategory(new CreateCategoryRequest("Ropa", 20L));
        verify(productService).createProduct(new CreateProductRequest("Camisa", null, null, 20L, 30L, null));
        verify(productImageService).createProductImage(new CreateProductImageRequest(40L), 20L);
        verify(productVariantService).createProductVariant(new CreateProductVariantRequest(
                "CAM-01", null, null, new BigDecimal("10.00"), 2, 40L, 20L));
        verify(customerService).createCustomer(
                new CreateCustomerRequest(null, "+541112345678", null, 20L));
        verify(conversationService).createConversation(new CreateConversationRequest(20L, 50L, null));
        verify(messageService).createMessage(new CreateMessageRequest(60L), 20L);
        verify(whatsAppAccountService).createWhatsAppAccount(new CreateWhatsAppAccountRequest(20L));
    }

    @Test
    void rejectsInvalidRequestBodiesBeforeCallingServices() throws Exception {
        postJsonExpectBadRequest(merchants, "/api/merchants", """
                {"name":"","ownerUserId":30}
                """);
        postJsonExpectBadRequest(merchantUsers, "/api/merchant-users", """
                {"merchantId":20,"userId":30,"role":" "}
                """);
        postJsonExpectBadRequest(users, "/api/users", """
                {"email":"not-an-email"}
                """);
        postJsonExpectBadRequest(categories, "/api/categories", """
                {"name":"","merchantId":20}
                """);
        postJsonExpectBadRequest(products, "/api/products", """
                {"name":" ","merchantId":20}
                """);
        postJsonExpectBadRequest(products, "/api/products", """
                {"name":"Camisa","merchantId":20}
                """);
        postJsonExpectBadRequest(productImages, "/api/product-images", """
                {}
                """);
        postJsonExpectBadRequest(productVariants, "/api/product-variants", """
                {"sku":"","price":-1,"stock":-1,"productId":40,"merchantId":20}
                """);
        postJsonExpectBadRequest(customers, "/api/customers", """
                {"phone":"","merchantId":20}
                """);
        postJsonExpectBadRequest(conversations, "/api/conversations", """
                {"merchantId":20}
                """);
        postJsonExpectBadRequest(messages, "/api/messages", """
                {}
                """);
        postJsonExpectBadRequest(whatsAppAccounts, "/api/whatsapp-accounts", """
                {}
                """);

        verifyNoInteractions(merchantService, merchantUserService, userService, categoryService, productService,
                productImageService, productVariantService, customerService, conversationService, messageService,
                whatsAppAccountService);
    }

    @Test
    void requiresMerchantIdForMerchantScopedGetEndpoints() throws Exception {
        categories.perform(get("/api/categories")).andExpect(status().isBadRequest());
        conversations.perform(get("/api/conversations")).andExpect(status().isBadRequest());
        customers.perform(get("/api/customers")).andExpect(status().isBadRequest());
        messages.perform(get("/api/messages")).andExpect(status().isBadRequest());
        products.perform(get("/api/products")).andExpect(status().isBadRequest());
        productImages.perform(get("/api/product-images")).andExpect(status().isBadRequest());
        productVariants.perform(get("/api/product-variants")).andExpect(status().isBadRequest());
        users.perform(get("/api/users")).andExpect(status().isBadRequest());
        whatsAppAccounts.perform(get("/api/whatsapp-accounts")).andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService, conversationService, customerService, messageService, productService,
                productImageService, productVariantService, userService, whatsAppAccountService);
    }

    @Test
    void deletesProductUsingMerchantScopedEndpoint() throws Exception {
        products.perform(delete("/api/products")
                .param("merchantId", "20")
                .param("id", "40"))
                .andExpect(status().isNoContent());
        products.perform(delete("/api/products").param("merchantId", "20"))
                .andExpect(status().isBadRequest());

        verify(productService).deleteProduct(20L, 40L);
    }

    private static MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller).build();
    }

    private static void postJson(MockMvc mvc, String uri, String body, String... queryParameters) throws Exception {
        var request = post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
        for (int i = 0; i < queryParameters.length; i += 2) {
            request.param(queryParameters[i], queryParameters[i + 1]);
        }
        mvc.perform(request)
                .andExpect(status().isCreated());
    }

    private static void postJsonExpectBadRequest(MockMvc mvc, String uri, String body) throws Exception {
        mvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
}
