package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WikiService {
    private final WikiRepository wikiRepository;
    private final AccountLogService accountLogService;

    public WikiService(WikiRepository wikiRepository, AccountLogService accountLogService) {
        this.wikiRepository = wikiRepository;
        this.accountLogService = accountLogService;
    }

    public WikiPage loadById(Long id) throws Exception{
        Optional<WikiPage> optionalWikiPage = wikiRepository.findById(id);
        try{
            return optionalWikiPage.get();
        }catch (Exception e){
            throw new Exception("Wiki Page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<WikiPage> findWikiByCategory(String category){
        return wikiRepository.findByCategory(category);
    }

    public List<WikiPage> getAll(){
        return wikiRepository.findAll();
    }

    public List<WikiPage> getByApproval(boolean enabled){
        return wikiRepository.findByEnabled(enabled);
    }

    public void delete(Long id, User requester) throws Exception {
        Optional<WikiPage> wikiPage = wikiRepository.findById(id);
        try{
            WikiPage page = wikiPage.get();
            if(requester.isAdmin() || requester.isOwner() || requester.getId().equals(page.getAuthor().getId())){
                wikiRepository.deleteById(id);
            }else{
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }
        }catch (Exception e){
            throw new Exception("Wiki page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public void save(WikiPage wikiPage, User requester) throws Exception {
        if(requester.isAdmin() || requester.isOwner() || requester.getId().equals(wikiPage.getAuthor().getId())){
            wikiPage.setLastEdited(LocalDateTime.now());

            //Add to log
            AccountLog log = new AccountLog("Wiki page created/changed", wikiPage.getTitle() + " is saved.", wikiPage.getAuthor());
            accountLogService.save(log);

            wikiRepository.save(wikiPage);
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }

    public void enable(WikiPage wikiPage, boolean enable, User requester) throws Exception {
        if(requester.isAdmin() || requester.isOwner() || requester.getId().equals(wikiPage.getAuthor().getId())){
            wikiRepository.setEnabledById(wikiPage.getId(), enable);
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }
}
