package it.aulab.chronicles.Repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import it.aulab.chronicles.Model.Category;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Long> {

    


}
