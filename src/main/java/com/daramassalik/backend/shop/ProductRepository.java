package com.daramassalik.backend.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByNameAsc();
    List<Product> findByActiveTrueAndCategoryIgnoreCaseOrderByNameAsc(String category);

    @Query("""
            select p from Product p
            where p.active = true
              and (lower(p.name) like lower(concat('%', :search, '%'))
                   or lower(p.reference) like lower(concat('%', :search, '%')))
            order by p.name
            """)
    List<Product> searchCatalog(@Param("search") String search);
}
