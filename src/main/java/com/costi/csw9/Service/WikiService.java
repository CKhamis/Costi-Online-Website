package com.costi.csw9.Service;

import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<WikiPage> getWikiPagesByCat(WikiCategory category){
        return wikiRepository.findByCategory(category);
    }

    public List<WikiPage> getAll(){
        return wikiRepository.findAll();
    }

    public void delete(WikiPage wikiPage){
        wikiRepository.delete(wikiPage);
    }

    public void save(WikiPage wikiPage){
        wikiRepository.save(wikiPage);
    }

}
