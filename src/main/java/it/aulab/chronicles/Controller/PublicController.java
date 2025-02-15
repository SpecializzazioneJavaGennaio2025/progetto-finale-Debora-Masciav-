package it.aulab.chronicles.Controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.Service.ArticleService;

@Controller
public class PublicController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/")
    public String homepage(Model viewModel) {
        List<ArticleDTO> articles = articleService.readAll();
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());

        List<ArticleDTO> lastArticles = articles.stream().limit(4).collect(Collectors.toList());
        viewModel.addAttribute("articles", lastArticles);

        return "homepage";
    }

    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {
        if (number == 403) {
            return "redirect:/?notAuthorized";
        }
        return "redirect:/";
    }

}
