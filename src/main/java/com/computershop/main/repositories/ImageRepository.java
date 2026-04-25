package com.computershop.main.repositories;

import com.computershop.main.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    
    Optional<Image> findByImageUrl(String imageUrl);
    
    List<Image> findByImageUrlContaining(String keyword);
    boolean existsByImageUrl(String imageUrl);

    @Query("SELECT i FROM Image i WHERE i.imageUrl LIKE :pattern")
    List<Image> findByImageUrlPattern(@Param("pattern") String pattern);
}