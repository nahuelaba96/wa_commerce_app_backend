package com.example.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.CreateCategoryRequest;
import com.example.backend.dto.CreateConversationRequest;
import com.example.backend.dto.CreateCustomerRequest;
import com.example.backend.dto.CreateMerchantRequest;
import com.example.backend.dto.CreateMerchantUserRequest;
import com.example.backend.dto.CreateMessageRequest;
import com.example.backend.dto.CreateProductImageRequest;
import com.example.backend.dto.CreateProductRequest;
import com.example.backend.dto.CreateProductVariantRequest;
import com.example.backend.dto.CreateProductVariantRequestFromProduct;
import com.example.backend.dto.CreateUserRequest;
import com.example.backend.dto.CreateWhatsAppAccountRequest;
import com.example.backend.dto.MerchantResponse;
import com.example.backend.entity.Category;
import com.example.backend.entity.Conversation;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Merchant;
import com.example.backend.entity.MerchantUser;
import com.example.backend.entity.Message;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductImage;
import com.example.backend.entity.ProductVariant;
import com.example.backend.entity.User;
import com.example.backend.entity.WhatsAppAccount;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.mapper.ConversationMapper;
import com.example.backend.mapper.CustomerMapper;
import com.example.backend.mapper.MerchantMapper;
import com.example.backend.mapper.MerchantUserMapper;
import com.example.backend.mapper.MessageMapper;
import com.example.backend.mapper.ProductImageMapper;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.mapper.ProductVariantMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.mapper.WhatsAppAccountMapper;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.MerchantUserRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.ProductImageRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ProductVariantRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.WhatsAppAccountRepository;
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

class BackendServiceTest {

    @Test
    void categoryCreationAssociatesTheRequestedMerchant() {
        CategoryRepository categories = mock(CategoryRepository.class);
        CategoryMapper mapper = mock(CategoryMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        Category category = new Category();
        Merchant merchant = merchant(20L);
        CreateCategoryRequest request = new CreateCategoryRequest("Ropa", 20L);

        when(mapper.toEntity(request)).thenReturn(category);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(categories.save(category)).thenReturn(category);

        new CategoryService(categories, mapper, merchants).createCategory(request);

        assertSame(merchant, category.getMerchant());
        verify(categories).save(category);
    }

    @Test
    void customerCreationAssociatesTheRequestedMerchant() {
        CustomerRepository customers = mock(CustomerRepository.class);
        CustomerMapper mapper = mock(CustomerMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        Customer customer = new Customer();
        Merchant merchant = merchant(20L);
        CreateCustomerRequest request = new CreateCustomerRequest("Ana", "+541112345678", null, 20L);

        when(mapper.toEntity(request)).thenReturn(customer);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(customers.save(customer)).thenReturn(customer);

        new CustomerService(customers, mapper, merchants).createCustomer(request);

        assertSame(merchant, customer.getMerchant());
        verify(customers).save(customer);
    }

    @Test
    void whatsappAccountCreationAssociatesTheRequestedMerchant() {
        WhatsAppAccountRepository accounts = mock(WhatsAppAccountRepository.class);
        WhatsAppAccountMapper mapper = mock(WhatsAppAccountMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        WhatsAppAccount account = new WhatsAppAccount();
        Merchant merchant = merchant(20L);
        CreateWhatsAppAccountRequest request = new CreateWhatsAppAccountRequest(20L);

        when(mapper.toEntity(request)).thenReturn(account);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(accounts.save(account)).thenReturn(account);

        new WhatsAppAccountService(accounts, mapper, merchants).createWhatsAppAccount(request);

        assertSame(merchant, account.getMerchant());
        verify(accounts).save(account);
    }

    @Test
    void conversationCreationRejectsCustomerFromAnotherMerchant() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        ConversationMapper mapper = mock(ConversationMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        CustomerRepository customers = mock(CustomerRepository.class);
        WhatsAppAccountRepository accounts = mock(WhatsAppAccountRepository.class);
        Merchant merchant = merchant(20L);
        Customer foreignCustomer = customer(50L, 21L);
        CreateConversationRequest request = new CreateConversationRequest(20L, 50L, null);

        when(mapper.toEntity(request)).thenReturn(new Conversation());
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(customers.findById(50L)).thenReturn(Optional.of(foreignCustomer));

        assertThrows(RuntimeException.class, () -> new ConversationService(
                conversations, mapper, merchants, customers, accounts).createConversation(request));

        verifyNoInteractions(conversations);
    }

    @Test
    void conversationCreationAssociatesMerchantCustomerAndMatchingWhatsappAccount() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        ConversationMapper mapper = mock(ConversationMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        CustomerRepository customers = mock(CustomerRepository.class);
        WhatsAppAccountRepository accounts = mock(WhatsAppAccountRepository.class);
        Merchant merchant = merchant(20L);
        Customer customer = customer(50L, 20L);
        WhatsAppAccount account = new WhatsAppAccount();
        account.setMerchant(merchant);
        Conversation conversation = new Conversation();
        CreateConversationRequest request = new CreateConversationRequest(20L, 50L, 70L);
        when(mapper.toEntity(request)).thenReturn(conversation);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(customers.findById(50L)).thenReturn(Optional.of(customer));
        when(accounts.findById(70L)).thenReturn(Optional.of(account));
        when(conversations.save(conversation)).thenReturn(conversation);

        new ConversationService(conversations, mapper, merchants, customers, accounts)
                .createConversation(request);

        assertSame(merchant, conversation.getMerchant());
        assertSame(customer, conversation.getCustomer());
        assertSame(account, conversation.getWhatsappAccount());
        verify(conversations).save(conversation);
    }

    @Test
    void conversationCreationRejectsWhatsappAccountFromAnotherMerchant() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        ConversationMapper mapper = mock(ConversationMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        CustomerRepository customers = mock(CustomerRepository.class);
        WhatsAppAccountRepository accounts = mock(WhatsAppAccountRepository.class);
        Merchant merchant = merchant(20L);
        Customer customer = customer(50L, 20L);
        WhatsAppAccount foreignAccount = new WhatsAppAccount();
        foreignAccount.setMerchant(merchant(21L));
        CreateConversationRequest request = new CreateConversationRequest(20L, 50L, 70L);

        when(mapper.toEntity(request)).thenReturn(new Conversation());
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(customers.findById(50L)).thenReturn(Optional.of(customer));
        when(accounts.findById(70L)).thenReturn(Optional.of(foreignAccount));

        assertThrows(RuntimeException.class, () -> new ConversationService(
                conversations, mapper, merchants, customers, accounts).createConversation(request));

        verifyNoInteractions(conversations);
    }

    @Test
    void productVariantCreationRequiresProductToBelongToMerchant() {
        ProductVariantRepository variants = mock(ProductVariantRepository.class);
        ProductVariantMapper mapper = mock(ProductVariantMapper.class);
        ProductRepository products = mock(ProductRepository.class);
        CreateProductVariantRequest request = new CreateProductVariantRequest(
                "CAM-01", null, null, new BigDecimal("10.00"), 2, 40L, 20L);
        when(mapper.toEntity(request)).thenReturn(new ProductVariant());
        when(products.findByIdAndMerchant_Id(40L, 20L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> new ProductVariantService(variants, mapper, products).createProductVariant(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(variants);
    }

    @Test
    void productVariantCreationUsesMerchantOwnedProduct() {
        ProductVariantRepository variants = mock(ProductVariantRepository.class);
        ProductVariantMapper mapper = mock(ProductVariantMapper.class);
        ProductRepository products = mock(ProductRepository.class);
        Product product = new Product();
        ProductVariant variant = new ProductVariant();
        CreateProductVariantRequest request = new CreateProductVariantRequest(
                "CAM-01", "M", "azul", new BigDecimal("10.00"), 2, 40L, 20L);
        when(mapper.toEntity(request)).thenReturn(variant);
        when(products.findByIdAndMerchant_Id(40L, 20L)).thenReturn(Optional.of(product));
        when(variants.save(variant)).thenReturn(variant);

        new ProductVariantService(variants, mapper, products).createProductVariant(request);

        assertSame(product, variant.getProduct());
        verify(variants).save(variant);
    }

    @Test
    void productCreationAttachesVariantsAndMerchantOwnedCategory() {
        ProductRepository products = mock(ProductRepository.class);
        ProductMapper mapper = mock(ProductMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        CategoryRepository categories = mock(CategoryRepository.class);
        Merchant merchant = merchant(20L);
        Category category = new Category();
        category.setMerchant(merchant);
        Product product = new Product();
        CreateProductVariantRequestFromProduct inputVariant = new CreateProductVariantRequestFromProduct(
                "CAM-01", "M", "azul", new BigDecimal("10.00"), 2);
        CreateProductRequest request = new CreateProductRequest("Camisa", null, null, 20L, 30L,
                List.of(inputVariant));
        when(mapper.toEntity(request)).thenReturn(product);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(categories.findById(30L)).thenReturn(Optional.of(category));
        when(products.save(product)).thenReturn(product);

        new ProductService(products, mapper, merchants, categories).createProduct(request);

        assertSame(merchant, product.getMerchant());
        assertSame(category, product.getCategory());
        assertEquals(1, product.getProductVariants().size());
        ProductVariant variant = product.getProductVariants().get(0);
        assertSame(product, variant.getProduct());
        assertEquals("CAM-01", variant.getSku());
        assertEquals("M", variant.getSize());
        assertEquals("azul", variant.getColor());
        assertEquals(new BigDecimal("10.00"), variant.getPrice());
        assertEquals(2, variant.getStock());
        verify(products).save(product);
    }

    @Test
    void productCreationRejectsCategoryFromAnotherMerchant() {
        ProductRepository products = mock(ProductRepository.class);
        ProductMapper mapper = mock(ProductMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        CategoryRepository categories = mock(CategoryRepository.class);
        Merchant merchant = merchant(20L);
        Category foreignCategory = new Category();
        foreignCategory.setMerchant(merchant(21L));
        CreateProductRequest request = new CreateProductRequest("Camisa", null, null, 20L, 30L, null);
        when(mapper.toEntity(request)).thenReturn(new Product());
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(categories.findById(30L)).thenReturn(Optional.of(foreignCategory));

        assertThrows(RuntimeException.class,
                () -> new ProductService(products, mapper, merchants, categories).createProduct(request));

        verifyNoInteractions(products);
    }

    @Test
    void merchantCreationAddsOwnerMembership() {
        MerchantRepository merchants = mock(MerchantRepository.class);
        MerchantMapper mapper = mock(MerchantMapper.class);
        MerchantUserRepository memberships = mock(MerchantUserRepository.class);
        UserRepository users = mock(UserRepository.class);
        Merchant merchant = merchant(20L);
        User owner = new User();
        CreateMerchantRequest request = new CreateMerchantRequest("Tienda", 30L);
        when(mapper.toEntity(request)).thenReturn(merchant);
        when(merchants.save(merchant)).thenReturn(merchant);
        when(users.findById(30L)).thenReturn(Optional.of(owner));

        new MerchantService(merchants, mapper, memberships, users).createMerchant(request);

        verify(memberships).save(org.mockito.ArgumentMatchers.argThat(membership ->
                membership.getMerchant() == merchant
                        && membership.getUser() == owner
                        && "OWNER".equals(membership.getRole())));
    }

    @Test
    void addingDuplicateMerchantMembershipIsRejectedWithoutSaving() {
        MerchantUserRepository memberships = mock(MerchantUserRepository.class);
        MerchantUserMapper mapper = mock(MerchantUserMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        UserRepository users = mock(UserRepository.class);
        CreateMerchantUserRequest request = new CreateMerchantUserRequest(20L, 30L, "ADMIN");
        when(memberships.existsByMerchant_IdAndUser_Id(20L, 30L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> new MerchantUserService(memberships, mapper, merchants, users).addUser(request));

        verifyNoInteractions(mapper, merchants, users);
        verify(memberships, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addingMerchantMembershipAssociatesMerchantAndUser() {
        MerchantUserRepository memberships = mock(MerchantUserRepository.class);
        MerchantUserMapper mapper = mock(MerchantUserMapper.class);
        MerchantRepository merchants = mock(MerchantRepository.class);
        UserRepository users = mock(UserRepository.class);
        Merchant merchant = merchant(20L);
        User user = new User();
        MerchantUser membership = new MerchantUser();
        CreateMerchantUserRequest request = new CreateMerchantUserRequest(20L, 30L, "ADMIN");
        when(memberships.existsByMerchant_IdAndUser_Id(20L, 30L)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(membership);
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(users.findById(30L)).thenReturn(Optional.of(user));
        when(memberships.save(membership)).thenReturn(membership);

        new MerchantUserService(memberships, mapper, merchants, users).addUser(request);

        assertSame(merchant, membership.getMerchant());
        assertSame(user, membership.getUser());
        verify(memberships).save(membership);
    }

    @Test
    void productImageAndMessageCreationAssociateTheirParentRecords() {
        ProductImageRepository images = mock(ProductImageRepository.class);
        ProductImageMapper imageMapper = mock(ProductImageMapper.class);
        ProductRepository products = mock(ProductRepository.class);
        Product product = new Product();
        ProductImage image = new ProductImage();
        CreateProductImageRequest imageRequest = new CreateProductImageRequest(40L);
        when(imageMapper.toEntity(imageRequest)).thenReturn(image);
        when(products.findByIdAndMerchant_Id(40L, 20L)).thenReturn(Optional.of(product));
        when(images.save(image)).thenReturn(image);

        new ProductImageService(images, imageMapper, products).createProductImage(imageRequest, 20L);

        assertSame(product, image.getProduct());
        verify(images).save(image);

        MessageRepository messages = mock(MessageRepository.class);
        MessageMapper messageMapper = mock(MessageMapper.class);
        ConversationRepository conversations = mock(ConversationRepository.class);
        Conversation conversation = new Conversation();
        Message message = new Message();
        CreateMessageRequest messageRequest = new CreateMessageRequest(60L);
        when(messageMapper.toEntity(messageRequest)).thenReturn(message);
        when(conversations.findByIdAndMerchant_Id(60L, 20L)).thenReturn(Optional.of(conversation));
        when(messages.save(message)).thenReturn(message);

        new MessageService(messages, messageMapper, conversations).createMessage(messageRequest, 20L);

        assertSame(conversation, message.getConversation());
        verify(messages).save(message);
    }

    @Test
    void userCreationPersistsTheRequestedEmail() {
        UserRepository users = mock(UserRepository.class);
        UserMapper mapper = mock(UserMapper.class);
        MerchantUserRepository memberships = mock(MerchantUserRepository.class);
        User user = new User();
        CreateUserRequest request = new CreateUserRequest("user@example.com");
        when(mapper.toEntity(request)).thenReturn(user);
        when(users.save(user)).thenReturn(user);

        new UserService(users, mapper, memberships).createUser(request);

        assertEquals("user@example.com", user.getEmail());
        verify(users).save(user);
    }

    @Test
    void merchantLookupMapsAnExistingMerchant() {
        MerchantRepository merchants = mock(MerchantRepository.class);
        MerchantMapper mapper = mock(MerchantMapper.class);
        Merchant merchant = merchant(20L);
        MerchantResponse response = new MerchantResponse(20L, "Tienda");
        when(merchants.findById(20L)).thenReturn(Optional.of(merchant));
        when(mapper.toResponse(merchant)).thenReturn(response);

        MerchantResponse actual = new MerchantService(
                merchants, mapper, mock(MerchantUserRepository.class), mock(UserRepository.class))
                .getMerchant(20L);

        assertEquals(response, actual);
        verify(merchants).findById(20L);
    }

    @Test
    void listOperationsQueryTheirExistingMerchantOrUserScope() {
        Long merchantId = 20L;

        CategoryRepository categories = mock(CategoryRepository.class);
        new CategoryService(categories, mock(CategoryMapper.class), mock(MerchantRepository.class))
                .getCategories(merchantId);
        verify(categories).findAllByMerchant_Id(merchantId);

        ConversationRepository conversations = mock(ConversationRepository.class);
        new ConversationService(conversations, mock(ConversationMapper.class), mock(MerchantRepository.class),
                mock(CustomerRepository.class), mock(WhatsAppAccountRepository.class)).getConversations(merchantId);
        verify(conversations).findAllByMerchant_Id(merchantId);

        CustomerRepository customers = mock(CustomerRepository.class);
        new CustomerService(customers, mock(CustomerMapper.class), mock(MerchantRepository.class))
                .getCustomers(merchantId);
        verify(customers).findAllByMerchant_Id(merchantId);

        MerchantUserRepository merchantMemberships = mock(MerchantUserRepository.class);
        new MerchantUserService(merchantMemberships, mock(MerchantUserMapper.class), mock(MerchantRepository.class),
                mock(UserRepository.class)).getMemberships(merchantId);
        verify(merchantMemberships).findAllByMerchant_Id(merchantId);

        MessageRepository messages = mock(MessageRepository.class);
        new MessageService(messages, mock(MessageMapper.class), mock(ConversationRepository.class))
                .getMessages(merchantId);
        verify(messages).findAllByConversation_Merchant_Id(merchantId);

        ProductRepository products = mock(ProductRepository.class);
        new ProductService(products, mock(ProductMapper.class), mock(MerchantRepository.class),
                mock(CategoryRepository.class)).getProducts(merchantId);
        verify(products).findAllByMerchant_Id(merchantId);

        ProductImageRepository images = mock(ProductImageRepository.class);
        new ProductImageService(images, mock(ProductImageMapper.class), mock(ProductRepository.class))
                .getProductImages(merchantId);
        verify(images).findAllByProduct_Merchant_Id(merchantId);

        ProductVariantRepository variants = mock(ProductVariantRepository.class);
        new ProductVariantService(variants, mock(ProductVariantMapper.class), mock(ProductRepository.class))
                .getProductVariants(merchantId);
        verify(variants).findAllByProduct_Merchant_Id(merchantId);

        WhatsAppAccountRepository accounts = mock(WhatsAppAccountRepository.class);
        new WhatsAppAccountService(accounts, mock(WhatsAppAccountMapper.class), mock(MerchantRepository.class))
                .getWhatsAppAccounts(merchantId);
        verify(accounts).findAllByMerchant_Id(merchantId);

        UserRepository users = mock(UserRepository.class);
        MerchantUserRepository userMemberships = mock(MerchantUserRepository.class);
        new UserService(users, mock(UserMapper.class), userMemberships).getUsers(merchantId);
        verify(userMemberships).findAllByMerchant_Id(merchantId);

        MerchantUserRepository merchantMembershipRepository = mock(MerchantUserRepository.class);
        UUID authUserId = UUID.randomUUID();
        new MerchantService(mock(MerchantRepository.class), mock(MerchantMapper.class), merchantMembershipRepository,
                mock(UserRepository.class))
                .getMerchantsByAuthUserID(authUserId);
        verify(merchantMembershipRepository).findAllByUser_AuthUserId(authUserId.toString());
    }

    private static Merchant merchant(Long id) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        return merchant;
    }

    private static Customer customer(Long id, Long merchantId) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setMerchant(merchant(merchantId));
        return customer;
    }
}
