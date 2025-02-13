package it.aulab.chronicles.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.aulab.chronicles.Model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Modifying
    @Query(value= "DELETE FROM image WHERE path = :path", nativeQuery = true)
    void deleteByPath(@Param("path") String path);
}
