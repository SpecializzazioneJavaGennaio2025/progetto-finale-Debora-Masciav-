package it.aulab.chronicles.Repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Model.User;


@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long>{
    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);
}
