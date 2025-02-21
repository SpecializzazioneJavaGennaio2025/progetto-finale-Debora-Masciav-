package it.aulab.chronicles.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Model.User;


@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long>{
    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);
    List<Article> findByIsAcceptedTrue();
    List<Article> findByIsAcceptedFalse();
    List<Article> findByIsAcceptedNull();
    Optional<Article> findBySlug(String slug);
    List<Article> findAllBySlug(String slug);
    boolean existsBySlug(String slug);


    @Query("SELECT a FROM Article a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.body) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY a.title ASC"
            )
    List<Article> search(@Param("searchTerm") String searchTerm);


    @Query("SELECT COUNT(a) FROM Article a where a.slug like concat(:slug, '%')")
    int getMaxSlug(@Param("slug") String slug);
}
