package it.aulab.chronicles.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "categories")

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotEmpty(message = "Il nome non puè essere vuoto")
    @Size(min = 2, max = 50)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<Article>();

}
