package it.aulab.chronicles.Service;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.User;
import it.aulab.chronicles.Repository.ArticleRepository;
import it.aulab.chronicles.Repository.UserRepository;

@Service
public class ArticleService implements CrudService<ArticleDTO, Article, Long>{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<ArticleDTO> readAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readAll'");
    }

    @Override
    public ArticleDTO read(Long key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public ArticleDTO create(Article article, Principal principal, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        ArticleDTO dto = modelMapper.map(articleRepository.save(article), ArticleDTO.class);
        return dto;
    }

    @Override
    public ArticleDTO update(Long key, Article model, MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Long key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
