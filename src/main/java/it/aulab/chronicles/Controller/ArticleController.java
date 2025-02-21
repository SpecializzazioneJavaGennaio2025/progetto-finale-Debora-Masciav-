package it.aulab.chronicles.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;
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
                String slug = articleService.generateUniqueSlug(article.getTitle(), article.getPublishDate());
    article.setSlug(slug);
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

    // @GetMapping(value= "/detail/{id}")
    // public String articleShow(@PathVariable("id") Long id, Model viewModel) {
    //     viewModel.addAttribute("title", "Dettaglio articolo");
    //     viewModel.addAttribute("article", articleService.read(id));
    //     return "article/detail";
    // }

    @GetMapping(value= "/detail/{id}")
    public String articleShow(@PathVariable("id") String id, Model viewModel, RedirectAttributes redirectAttributes) {
        Article article = null;
        try {
            Long articleId = Long.parseLong(id);
             article = articleRepository.findById(articleId).
            orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Articolo non trovato"));;
            
          

        } catch (NumberFormatException e) {
             article = articleRepository.findBySlug(id).get();
    
        }

        if (article != null) {
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("title", "Dettaglio articolo");
            return "article/detail";
        } else {
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagina non trovata");
            redirectAttributes.addFlashAttribute("errorMessage", "Pagina non trovata");
           return "redirect:/article/index";
        }
    }


   

    @GetMapping("/edit/{id}")
    public String articleEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Modifica l'articolo");
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }

    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("article") Article article,
            BindingResult result, RedirectAttributes redirectAttributes, Principal principal, MultipartFile file,
            Model viewModel) {
                if (result.hasErrors()) {
                    viewModel.addAttribute("title", "Modifica dell'articolo");
                    article.setImage(articleService.read(id).getImage());
                    viewModel.addAttribute("article", article);
                    viewModel.addAttribute("categories", categoryService.readAll());
                    redirectAttributes.addFlashAttribute("errorMessage", "C'è stato un errore, riprova!");
                    return "article/edit";
                }
                

                articleService.update(id, article, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo modificato con successo!");

        return "redirect:/article/index";
    }

    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo cancellato!");
        return "redirect:/writer/dashboard";
    }

    @GetMapping("/revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Dettaglio articolo");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId,
            RedirectAttributes redirectAttributes) {
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

    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli corrispondenti");
        List<ArticleDTO> articles = articleService.search(keyword);
        List<ArticleDTO> acceptedArticles = articles.stream()
                .filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/index";
    }
}
