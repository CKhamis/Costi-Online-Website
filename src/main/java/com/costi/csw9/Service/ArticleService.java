package com.costi.csw9.Service;

import com.costi.csw9.Model.Article;
import com.costi.csw9.Repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }


    public List<Article> getArticles(){
        return articleRepository.findAll();
    }

    public void addNewArticle(Article article) {
        Optional<Article> match = articleRepository.findArticleByEmail(article.getEmail());

        if(match.isPresent()){
            throw new IllegalStateException("email taken");
        }else{
            articleRepository.save(article);
        }
    }

    public void deleteArticle(Long articleId) {
        if(articleRepository.existsById(articleId)){
            articleRepository.deleteById(articleId);
        }else{
            throw new IllegalStateException("Article with " + articleId + " does not exist");
        }

    }

    @Transactional
    public void modifyArticle(Long articleId, String name, String email) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new IllegalStateException("Article with " + articleId + " does not exist"));

        if(name != null && name.length() > 0 && !Objects.equals(article.getName(), name)){
            article.setName(name);
        }

        if(email != null && email.length() > 0 && !Objects.equals(article.getEmail(), email)){
            Optional<Article> match = articleRepository.findArticleByEmail(email);

            if(match.isPresent()){
                throw new IllegalStateException("email taken");
            }else{
                article.setEmail(email);
            }
        }
    }
}
