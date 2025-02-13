package it.aulab.chronicles.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import it.aulab.chronicles.Model.Article;

public interface ImageService {

    void saveImageOnDb(String url, Article article);

    CompletableFuture<String> saveImageOnCloud(MultipartFile file) throws Exception;
    void deleteImage(String imagePath) throws IOException;


}
