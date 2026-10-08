package com.example.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.backend.controller.ProductVariantController;
import com.example.backend.entity.Merchant;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductVariant;
import com.example.backend.mapper.ProductVariantMapper;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ProductVariantRepository;
import com.example.backend.service.ProductVariantService;

class ProductVariantControllerTest {
    private final ProductVariantRepository variantRepository = mock(ProductVariantRepository.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductVariantService service = new ProductVariantService(
                variantRepository,
                new ProductVariantMapper(),
                mock(ProductRepository.class));
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductVariantController(service)).build();
    }

    @Test
    void deletesVariantForItsMerchant() throws Exception {
        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        when(variantRepository.findByIdAndProduct_Merchant_Id(10L, 20L)).thenReturn(Optional.of(variant));

        mockMvc.perform(delete("/api/product-variants")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNoContent());

        verify(variantRepository).delete(variant);
    }

    @Test
    void returnsNotFoundWhenVariantDoesNotExist() throws Exception {
        when(variantRepository.findByIdAndProduct_Merchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/product-variants")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(
                        "No se encontró la variante con ID: 10 para el comercio indicado",
                        result.getResponse().getErrorMessage()));
    }

    @Test
    void returnsNotFoundWhenVariantBelongsToAnotherMerchant() throws Exception {
        when(variantRepository.findByIdAndProduct_Merchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/product-variants")
                .param("merchantId", "20")
                .param("id", "10"))
                .andExpect(status().isNotFound());

        verify(variantRepository).findByIdAndProduct_Merchant_Id(10L, 20L);
    }

    @Test
    void updatesVariantFieldsWithoutChangingItsProduct() throws Exception {
        Product product = new Product();
        product.setId(30L);
        Merchant merchant = new Merchant();
        merchant.setId(20L);
        product.setMerchant(merchant);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setProduct(product);

        when(variantRepository.findByIdAndProduct_Merchant_Id(10L, 20L)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(ProductVariant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/product-variants")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "sku": "SKU-2",
                          "size": "M",
                          "color": "Azul",
                          "price": 19.99,
                          "stock": 5
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.sku").value("SKU-2"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.productId").value(30));

        verify(variantRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getSku().equals("SKU-2")
                        && saved.getSize().equals("M")
                        && saved.getColor().equals("Azul")
                        && saved.getPrice().compareTo(new BigDecimal("19.99")) == 0
                        && saved.getStock() == 5
                        && saved.getProduct() == product));
    }

    @Test
    void returnsNotFoundWhenUpdatingVariantOutsideMerchant() throws Exception {
        when(variantRepository.findByIdAndProduct_Merchant_Id(10L, 20L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/product-variants")
                .param("merchantId", "20")
                .param("id", "10")
                .contentType("application/json")
                .content("""
                        {
                          "sku": "SKU-2",
                          "size": null,
                          "color": null,
                          "price": 19.99,
                          "stock": 5
                        }
                        """))
                .andExpect(status().isNotFound());
    }
}
