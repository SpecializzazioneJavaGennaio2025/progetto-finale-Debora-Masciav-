package it.aulab.chronicles.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.aulab.chronicles.DTO.CategoryDTO;
import it.aulab.chronicles.Model.Category;


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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CategoryDTO update(Long key, Category model, MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Long key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
