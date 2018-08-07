package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public interface ProblemSetService {

    ProblemSetRevision getLatestProblemSet(Long editUrl);

    ProblemSetRevision getProblemSetByShareUrl(Long shareUrl);

    ProblemSetRevision saveNewVersionOfProblemSet(ProblemSet problemSet) throws IllegalArgumentException;

    ProblemSetDTO findProblemsByUrlCode(String code) throws IllegalArgumentException;
}
