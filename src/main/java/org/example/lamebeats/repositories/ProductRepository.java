package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByStockQuantityLessThan(Integer minQuantity);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.price < :price AND p.stockQuantity > :quantity")
    List<Product> findProductsOnSale(@Param("price") BigDecimal price, @Param("quantity") Integer quantity);

    boolean existsBySku(String sku);
}