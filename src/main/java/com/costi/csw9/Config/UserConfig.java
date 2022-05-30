package com.costi.csw9.Config;

import com.costi.csw9.Model.Article;
import com.costi.csw9.Repository.ArticleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(ArticleRepository repository){
        return args -> {
            Article rat = new Article("img.png", "title", "description", "words", LocalDate.of(2000, Month.JULY, 13), "costi@rat.com");
            Article rat2 = new Article("img.png", "rtertert", "asdf", "", LocalDate.of(2344, Month.JULY, 13), "shitasdf");

            repository.saveAll(List.of(rat, rat2));
        };
    }
}
