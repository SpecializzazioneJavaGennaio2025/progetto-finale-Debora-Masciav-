package it.aulab.chronicles.Controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.DTO.UserDTO;
import it.aulab.chronicles.Model.User;
import it.aulab.chronicles.Repository.ArticleRepository;
import it.aulab.chronicles.Repository.CareerRequestRepository;
import it.aulab.chronicles.Service.ArticleService;
import it.aulab.chronicles.Service.CategoryService;
import it.aulab.chronicles.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @SuppressWarnings("null")
    @PostMapping("/auth/store")
    public String store(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response){
        User existingUser = userService.findUserByEmail(userDTO.getEmail());
        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null, "There is already an account registered with the same email");
        } 

        if(result.hasErrors()){
            model.addAttribute("user", userDTO);
            return "auth/register";
        }

        userService.saveUser(userDTO, redirectAttributes, request, response);
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully");
        return "redirect:/";
    }


    @GetMapping("search/{id}")
    public String userArticleSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "Tutti gli articoli dell'utente: " + user.getUsername());

        List<ArticleDTO> articles = articleService.searchByAuthor(user);

        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);

        return "article/index";
    }


    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Richieste ricevute:");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse().stream().filter(request -> !request.getIsReviewed()).collect(Collectors.toList()));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "admin/dashboard";
    }


    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Articoli da revisionare");
        viewModel.addAttribute("articles", articleRepository.findByIsAcceptedNull());
        return "revisor/dashboard";
    }

    @GetMapping("/writer/dashboard")
public String writerDashboard(Model viewModel, Principal principal){
    viewModel.addAttribute("title", "Articoli redatti");
    List<ArticleDTO> userArticles = articleService.readAll().stream().filter(article->article.getUser().getEmail().equals(principal.getName())).toList();
    viewModel.addAttribute("articles", userArticles);
    return "writer/dashboard";
}
    
}
