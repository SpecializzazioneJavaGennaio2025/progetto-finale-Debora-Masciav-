package it.aulab.chronicles.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.DTO.CategoryDTO;
import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Service.ArticleService;
import it.aulab.chronicles.Service.CategoryService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/search/{id}")
    public String categoryShow(@PathVariable("id") Long id, Model viewModel) {
        CategoryDTO category = categoryService.read(id);
        viewModel.addAttribute("title", "Tutti gli articoli della categoria: " + category.getName());

        List<ArticleDTO> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());


        viewModel.addAttribute("articles", acceptedArticles);

        return "article/index";

    }

    @GetMapping("/create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea una categoria");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    @PostMapping("/store")
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un categoria");
            viewModel.addAttribute("category", category);
            return "category/create";
        }

        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria aggiunta con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Modifica una categoria");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    @PostMapping("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Modifica una categoria");
            viewModel.addAttribute("category", category);
            return "category/update";
        }

        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria modificata con successo!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria eliminata con successo!");
        return "redirect:/admin/dashboard";
    }

}
