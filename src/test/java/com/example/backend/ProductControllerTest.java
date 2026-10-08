package com.example.backend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.controller.ProductController;
import com.example.backend.entity.Category;
import com.example.backend.entity.Merchant;
import com.example.backend.entity.Product;
import com.example.backend.mapper.ProductMapper;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MerchantRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.ProductService;

class ProductControllerTest {
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductService service = new ProductService(
                productRepository,
                new ProductMapper(),
                mock(MerchantRepository.class),
                categoryRepository);
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(new ProductController(service))
                .build();
    }

    @Test
    void deletesProductForItsMerchant() throws Exception {
        Product product = new Product();
        product.setId(10L);
        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.of(product));

        mockMvc.perform(delete("/api/products")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNoContent());

        verify(productRepository).delete(product);
    }

    @Test
    void returnsNotFoundWhenProductDoesNotExist() throws Exception {
        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(
                        "No se encontró el producto con ID: 10 para el comercio indicado",
                        result.getResponse().getErrorMessage()));
    }

    @Test
    void returnsNotFoundWhenProductBelongsToAnotherMerchant() throws Exception {
        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNotFound());

        verify(productRepository).findByIdAndMerchant_Id(10L, 20L);
    }

    @Test
    void updatesProductFieldsAndCategoryForItsMerchant() throws Exception {
        Product product = new Product();
        product.setId(10L);
        Merchant merchant = new Merchant();
        merchant.setId(20L);
        product.setMerchant(merchant);
        Category previousCategory = new Category();
        previousCategory.setId(29L);
        previousCategory.setMerchant(merchant);
        product.setCategory(previousCategory);

        Category updatedCategory = new Category();
        updatedCategory.setId(30L);
        updatedCategory.setName("Ropa");
        updatedCategory.setMerchant(merchant);

        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(30L)).thenReturn(Optional.of(updatedCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/products")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Camisa",
                          "description": "Algodón",
                          "brand": "Marca",
                          "categoryId": 30
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.name").value("Camisa"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.category.id").value(30));

        verify(productRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getName().equals("Camisa")
                        && saved.getDescription().equals("Algodón")
                        && saved.getBrand().equals("Marca")
                        && saved.getCategory() == updatedCategory
                        && saved.getMerchant() == merchant));
    }

    @Test
    void allowsOmittingBrandAndClearsItsValueWhenUpdatingProduct() throws Exception {
        Product product = new Product();
        product.setId(10L);
        product.setBrand("Marca anterior");
        Merchant merchant = new Merchant();
        merchant.setId(20L);
        product.setMerchant(merchant);

        Category category = new Category();
        category.setId(30L);
        category.setMerchant(merchant);

        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(30L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/products")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Camisa",
                          "description": null,
                          "categoryId": 30
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.brand").doesNotExist());

        verify(productRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getBrand() == null));
    }

    @Test
    void returnsNotFoundWhenUpdatingProductOutsideMerchant() throws Exception {
        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Camisa",
                          "description": null,
                          "brand": null,
                          "categoryId": 30
                        }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsCategoryFromAnotherMerchantWhenUpdatingProduct() throws Exception {
        Product product = new Product();
        product.setId(10L);
        Merchant merchant = new Merchant();
        merchant.setId(20L);
        product.setMerchant(merchant);

        Merchant anotherMerchant = new Merchant();
        anotherMerchant.setId(21L);
        Category foreignCategory = new Category();
        foreignCategory.setId(30L);
        foreignCategory.setMerchant(anotherMerchant);

        when(productRepository.findByIdAndMerchant_Id(10L, 20L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(30L)).thenReturn(Optional.of(foreignCategory));

        mockMvc.perform(put("/api/products")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Camisa",
                          "description": null,
                          "brand": null,
                          "categoryId": 30
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
