package com.costi.csw9.Service;

import com.costi.csw9.Model.AccountLog;
import com.costi.csw9.Model.DTO.WikiRequest;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.WikiPage;
import com.costi.csw9.Repository.WikiRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
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

    public WikiPage findById(Long id, User author) throws Exception{
        Optional<WikiPage> optionalWikiPage = wikiRepository.findById(id);
        if(optionalWikiPage.isPresent()){
            // Wiki Page already exists, check if the author matches
            WikiPage originalWikiPage = optionalWikiPage.get();

            if(originalWikiPage.isEnabled() || originalWikiPage.getAuthor().equals(author) || author.isOwner() || author.isAdmin()){
                return originalWikiPage;
            }else{
                throw new AccessDeniedException(LogicTools.INVALID_PERMISSIONS_MESSAGE);

            }
        }else{
            throw new NoSuchElementException("Wiki Page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public WikiPage save(WikiRequest request, User author){
        // Author must exist
        if(author == null){
            // No use logged in
            throw new AccessDeniedException("No user logged in");
        }

        // id must be null to create new pages
        if(request.getId() != null && request.getId() >= 0){
            Optional<WikiPage> optionalWikiPage = wikiRepository.findById(request.getId());
            if(optionalWikiPage.isPresent()){
                // Wiki Page already exists, check if the author matches
                WikiPage originalWikiPage = optionalWikiPage.get();

                if(originalWikiPage.getAuthor().equals(author) || author.isOwner() || author.isAdmin()){
                    originalWikiPage.userEditValues(request);
                    WikiPage savedPage = wikiRepository.save(originalWikiPage);

                    // Add this to log
                    AccountLog log = new AccountLog("Wiki page changed", savedPage.getTitle() + " is saved.", savedPage.getAuthor());
                    accountLogService.save(log);

                    return savedPage;
                }else{
                    //Author does not match
                    // Add this to log
                    AccountLog log = new AccountLog("Account tried to modify a wiki page", "Wiki: " + originalWikiPage.getTitle() + " of id " + originalWikiPage.getId(), author);
                    accountLogService.save(log);

                    throw new AccessDeniedException("Wiki Page" + LogicTools.INVALID_PERMISSIONS_MESSAGE);
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
            AccountLog log = new AccountLog("Wiki page created", savedPage.getTitle() + " is saved.", savedPage.getAuthor());
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

    public void delete(Long id, User requester) {
        Optional<WikiPage> wikiPage = wikiRepository.findById(id);
        try{
            WikiPage page = wikiPage.get();
            if(page.getAuthor().equals(requester)){
                wikiRepository.deleteById(id);

                // Document change
                AccountLog log = new AccountLog("Wiki Page Deleted", "User " + requester.getFirstName() + " " + requester.getLastName() + " has deleted a wiki page their own", requester);
                accountLogService.save(log);

            }else{
                throw new InsufficientAuthenticationException(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }
        }catch (Exception e){
            throw new NullPointerException("Wiki page" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }
}
