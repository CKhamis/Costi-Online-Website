package com.costi.csw9.Service;

import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WikiService {
    @Autowired
    private final WikiRepository wikiRepository;

    public WikiService(WikiRepository wikiRepository) {
        this.wikiRepository = wikiRepository;
    }


    public WikiPage loadById(Long id){
        return wikiRepository.findById(id);
    }
}
