package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WikiService {
    private final WikiRepository wikiRepository;
    private final AccountLogService accountLogService;

    public WikiService(WikiRepository wikiRepository, AccountLogService accountLogService) {
        this.wikiRepository = wikiRepository;
        this.accountLogService = accountLogService;
    }

    public List<WikiPage> findAll(){
        return wikiRepository.findByEnabled(true);
    }

    public WikiPage findById(Long id) throws Exception{
        Optional<WikiPage> optionalWikiPage = wikiRepository.findById(id);
        if(optionalWikiPage.isPresent()){
            WikiPage wikiPage = optionalWikiPage.get();
            if(wikiPage.isEnabled()){
                return wikiPage;
            }
            throw new AccessDeniedException("Wiki Page" + LogicTools.INVALID_PERMISSIONS_MESSAGE);

        }else{
            throw new NoSuchElementException("Wiki Page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public WikiPage save(WikiRequest request, User author) throws Exception{
        // id must be null to create new pages
        if(request.getId() != null && request.getId() >= 0){
            Optional<WikiPage> optionalWikiPage = wikiRepository.findById(request.getId());
            if(optionalWikiPage.isPresent()){
                // Wiki Page already exists, check if the author matches
                WikiPage originalWikiPage = optionalWikiPage.get();

                if(originalWikiPage.getAuthor().equals(author) || author.isOwner() || author.isAdmin()){
                    //TODO: check if the originalWikiPage.getAuthor().equals(author) works
                    originalWikiPage.userEditValues(request);

                }else{
                    //Author does not match
                    // Add this to log
                    AccountLog log = new AccountLog("Account tried to modify a wiki page", "Wiki: " + originalWikiPage.getTitle() + " of id " + originalWikiPage.getId(), author);
                    accountLogService.save(log);
                }
            }else{
                // Id is present, but not valid
                throw new NoSuchElementException("ID is invalid");
            }
        }else{
            // Wiki Page does not exist, create a new one
            WikiPage newPage = new WikiPage(author);
            newPage.userEditValues(request);
            WikiPage savedPage = wikiRepository.save(newPage);

            // Add this to log
            AccountLog log = new AccountLog("Wiki page created/changed", savedPage.getTitle() + " is saved.", savedPage.getAuthor());
            accountLogService.save(log);

            return savedPage;
        }
    }

    public void addView(WikiPage wikiPage) {
        wikiPage.setViews(wikiPage.getViews() + 1);
        wikiRepository.save(wikiPage);
    }

    //TODO: Below functions will be deprecated

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
            if(requester.isAdmin() || requester.isOwner()){
                wikiRepository.deleteById(id);
            }else{
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }
        }catch (Exception e){
            throw new Exception("Wiki page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public void deprecatedSave(WikiPage wikiPage, User requester) throws Exception {
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
        if (requester.isAdmin() || requester.isOwner()) {
            wikiRepository.setEnabledById(wikiPage.getId(), enable);
        } else {
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }
}
