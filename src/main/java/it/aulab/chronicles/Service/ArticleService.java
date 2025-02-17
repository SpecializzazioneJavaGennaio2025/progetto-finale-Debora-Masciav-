package it.aulab.chronicles.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.Category;
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

    @Autowired
    private ImageService imageService;

    @Override
    public List<ArticleDTO> readAll() {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findAll()) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));

            
        }
        return dtos;
    }

    @Override
    public ArticleDTO read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if (optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author id" + key + "not found");
        }
    }

    @Override
    public ArticleDTO create(Article article, Principal principal, MultipartFile file) {
        String url = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        if(!file.isEmpty()){
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url = futureUrl.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        article.setIsAccepted(null);

        ArticleDTO dto = modelMapper.map(articleRepository.save(article), ArticleDTO.class);
        if(!file.isEmpty()){
            imageService.saveImageOnDb(url, article);
        }
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

    public List<ArticleDTO> searchByCategory(Category category) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    public List<ArticleDTO> searchByAuthor(User user) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByUser(user)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    public void setIsAccepted(Boolean result, Long id){
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

}
