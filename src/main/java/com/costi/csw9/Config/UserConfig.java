package com.costi.csw9.Config;

import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository){
        return args -> {
            User rat = new User("rat", LocalDate.of(2000, Month.JULY, 13), "shitasdf");
            User rat2 = new User("rtertert", LocalDate.of(2344, Month.JULY, 13), "shitasdf");

            repository.saveAll(List.of(rat, rat2));
        };
    }
}
