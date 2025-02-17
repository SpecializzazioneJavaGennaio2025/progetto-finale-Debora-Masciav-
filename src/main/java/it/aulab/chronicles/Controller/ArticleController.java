package it.aulab.chronicles.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.DTO.CategoryDTO;
import it.aulab.chronicles.Model.Article;
import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Repository.ArticleRepository;
import it.aulab.chronicles.Service.ArticleService;
import it.aulab.chronicles.Service.CrudService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDTO, Category, Long> categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/index")
    public String articleIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");


        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        return "article/index";
    }

    @GetMapping("/create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    @PostMapping("/store")
    public String articleStore(@Valid @ModelAttribute("article") Article article, BindingResult result,
            RedirectAttributes redirectAttributes, Principal principal, MultipartFile file, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }

        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo aggiunto con successo!");
        return "redirect:/";
    }

    @GetMapping("/detail/{id}")
    public String articleShow(@PathVariable("id") Long id, Model viewModel) {

        System.out.println("DEBUG: id" + id);
        viewModel.addAttribute("title", "Dettaglio articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }


    @GetMapping("/revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("successMessage", "Articolo accettato!");
        } else if (action.equals("reject")) {
                articleService.setIsAccepted(false, articleId);
                redirectAttributes.addFlashAttribute("successMessage", "Articolo rifiutato!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Operazione non valida!");
        }
        return "redirect:/revisor/dashboard";
}

        

    }
