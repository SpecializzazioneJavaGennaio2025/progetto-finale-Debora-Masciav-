package it.aulab.chronicles.Controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Repository.ArticleRepository;
import it.aulab.chronicles.Service.ArticleService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/article")

public class SlugController {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/show/{slug}")
    // public void redirectToId(@PathVariable String slug, HttpServletResponse response) {
    //     Optional<Article> article = articleRepository.findBySlug(slug);
    //     if (article.isPresent()) {
    //         Long id = article.get().getId();
    //         try {
    //             response.sendRedirect("/article/detail/" + id);
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }

    // }
        // return ResponseEntity.notFound().build();
    // }

    public String articleShow(@PathVariable("slug")String slug, Model viewModel) {
        Optional<Article> article = articleRepository.findBySlug(slug);
        Long id = article.get().getId();
    
        viewModel.addAttribute("title", "Dettaglio articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }



}
