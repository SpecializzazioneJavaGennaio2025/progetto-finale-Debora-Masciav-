package it.aulab.chronicles.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.aulab.chronicles.DTO.CategoryDTO;
import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.Category;
import jakarta.transaction.Transactional;


@Service
public class CategoryService implements CrudService<CategoryDTO, Category, Long> {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private it.aulab.chronicles.Repository.CategoryRepository CategoryRepository;
    
    @Override
    public List<CategoryDTO> readAll() {
        List<CategoryDTO> dtos = new ArrayList<CategoryDTO>();
        for (Category category : CategoryRepository.findAll()) {
            dtos.add(modelMapper.map(category, CategoryDTO.class));
        }
        return dtos;
    }

    @Override
    public CategoryDTO read(Long key) {
        return modelMapper.map(CategoryRepository.findById(key), CategoryDTO.class);
    }

    @Override
    public CategoryDTO create(Category model, Principal principal, MultipartFile file) {
       return modelMapper.map(CategoryRepository.save(model), CategoryDTO.class);
    }

    @Override
    public CategoryDTO update(Long key, Category model, MultipartFile file) {
       if (CategoryRepository.existsById(key)) {
           model.setId(key);
            return modelMapper.map(CategoryRepository.save(model), CategoryDTO.class);
       } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
       }
    }

    @Override
    @Transactional
    public void delete(Long key) {
        if (CategoryRepository.existsById(key)) {
            Category category = CategoryRepository.findById(key).get();

            if(category.getArticles() != null) {
                Iterable<Article> articles = category.getArticles();
                for (Article article : articles) {
                    article.setCategory(null);
                }
            }

            CategoryRepository.deleteById(key);
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
            
        
    }

}
