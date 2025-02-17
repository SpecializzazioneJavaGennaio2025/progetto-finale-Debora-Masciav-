package it.aulab.chronicles.DTO;

import java.time.LocalDate;

import it.aulab.chronicles.Model.Category;
import it.aulab.chronicles.Model.Image;
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
    private Boolean isAccepted;
    private User user;
    private Category category;
    private Image image;
}
