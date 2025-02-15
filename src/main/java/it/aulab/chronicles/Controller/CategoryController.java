package it.aulab.chronicles.Controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.aulab.chronicles.DTO.ArticleDTO;
import it.aulab.chronicles.DTO.CategoryDTO;
import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Service.ArticleService;
import it.aulab.chronicles.Service.CategoryService;

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
        viewModel.addAttribute("articles", articles);

        return "article/index";

    }

}
