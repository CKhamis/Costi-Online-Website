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
            Article rat = new Article("rat", LocalDate.of(2000, Month.JULY, 13), "shitasdf");
            Article rat2 = new Article("rtertert", LocalDate.of(2344, Month.JULY, 13), "shitasdf");

            repository.saveAll(List.of(rat, rat2));
        };
    }
}
