package it.aulab.chronicles.DTO;

import java.time.LocalDate;
import java.util.Locale.Category;

import it.aulab.chronicles.Model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ArticleDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String body;
    private LocalDate publishDate;
    private User user;
    private Category category;
}
