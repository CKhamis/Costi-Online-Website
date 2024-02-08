package com.costi.csw9.Service;

import com.costi.csw9.Model.DynamicContent;
import com.costi.csw9.Repository.DynamicContentRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DynamicContentService {
    private final DynamicContentRepository dynamicContentRepository;

    private DynamicContentService(DynamicContentRepository dynamicContentRepository){
        this.dynamicContentRepository = dynamicContentRepository;
    }

    public DynamicContent getContent() {
        return  dynamicContentRepository.findTopByEnabledOrderByLastEditedDesc(true);
    }

}
