package it.aulab.chronicles.Service;

import java.security.Principal;
import java.time.LocalDate;
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
import it.aulab.chronicles.Utils.StringManipulation;

@Service
public class ArticleService implements CrudService<ArticleDTO, Article, Long> {
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
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        if (!file.isEmpty()) {
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url = futureUrl.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        String slug = generateUniqueSlug(article.getTitle(), article.getPublishDate());
        article.setSlug(slug);

        article.setIsAccepted(null);

        ArticleDTO dto = modelMapper.map(articleRepository.save(article), ArticleDTO.class);
        if (!file.isEmpty()) {
            imageService.saveImageOnDb(url, article);
        }
        return dto;
    }

    @Override
    public ArticleDTO update(Long key, Article updatedArticle, MultipartFile file) {
        String url = "";
        if (articleRepository.existsById(key)) {
            updatedArticle.setId(key);
            Article article = articleRepository.findById(key).get();
            updatedArticle.setUser(article.getUser());

            if (!file.isEmpty()) {
                try {
                    imageService.deleteImage(article.getImage().getPath());
                    try {
                        CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                        url = futureUrl.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageService.saveImageOnDb(url, updatedArticle);
                    updatedArticle.setIsAccepted(null);
                    return modelMapper.map(articleRepository.save(updatedArticle), ArticleDTO.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            // } else if (article.getImage() == null) {
            //     updatedArticle.setIsAccepted(article.getIsAccepted());
            } else {
                updatedArticle.setImage(article.getImage());
                if (updatedArticle.equals(article) == false) {
                    updatedArticle.setIsAccepted(null);
                } else {
                    updatedArticle.setIsAccepted(article.getIsAccepted());
                }

               

                return modelMapper.map(articleRepository.save(updatedArticle), ArticleDTO.class);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Override
    public void delete(Long key) {
        if (articleRepository.existsById(key)) {
            Article article = articleRepository.findById(key).get();
            try {
                String path = article.getImage().getPath();
                article.getImage().setArticle(null);
                imageService.deleteImage(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            articleRepository.deleteById(key);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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

    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

    public List<ArticleDTO> search(String keyword) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.search(keyword)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    // public String generateUniqueSlug(String title, LocalDate date) {
    // String baseSlug = StringManipulation.makeSlug(title);
    // String uniqueSlug = date + "-" + baseSlug;
    // int counter = 1;

    // while (articleRepository.existsBySlug(uniqueSlug)) {
    // uniqueSlug = baseSlug + "-" + counter;
    // counter++;
    // }

    // return uniqueSlug;
    // }

    public String generateUniqueSlug(String title, LocalDate date) {
        String baseSlug = StringManipulation.makeSlug(title);
        String uniqueSlug = date + "-" + baseSlug;
        int counter = articleRepository.getMaxSlug(uniqueSlug);
        if (counter > 0) {
            uniqueSlug = baseSlug + "-" + counter;
        }
        return uniqueSlug;
    }

}
