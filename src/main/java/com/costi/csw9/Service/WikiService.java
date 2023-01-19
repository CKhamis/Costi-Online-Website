package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WikiService {
    @Autowired
    private final WikiRepository wikiRepository;

    @Autowired
    private final AccountLogService accountLogService;

    public WikiService(WikiRepository wikiRepository, AccountLogService accountLogService) {
        this.wikiRepository = wikiRepository;
        this.accountLogService = accountLogService;
    }

    public WikiPage loadById(Long id){
        return wikiRepository.findById(id);
    }

    public List<WikiPage> getWikiPagesByCat(WikiCategory category){
        return wikiRepository.findByCategory(category.name());
    }

    public List<WikiPage> getWikiPagesByAuthor(Long id){
        return wikiRepository.findByAuthor(id);
    }

    public List<WikiPage> getWikiPagesByCat(String category){
        return wikiRepository.findByCategory(category);
    }

    public List<WikiPage> getAll(){
        return wikiRepository.findAll();
    }

    public List<WikiPage> getByApproval(boolean enabled){
        return wikiRepository.getByApproval(enabled);
    }

    public void delete(WikiPage wikiPage){
        wikiRepository.delete(wikiPage.getId());
    }

    public void save(WikiPage wikiPage){
        wikiPage.setLastEdited(LocalDateTime.now());

        //Add to log
        AccountLog log = new AccountLog("Wiki page created/changed", wikiPage.getTitle() + " is saved.", wikiPage.getAuthor());
        accountLogService.save(log);

        wikiRepository.save(wikiPage);
    }

    public void enable(WikiPage page, boolean enable) {
        wikiRepository.enable(page.getId(), enable);
    }
}
