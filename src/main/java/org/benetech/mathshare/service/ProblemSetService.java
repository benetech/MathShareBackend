package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;

public interface ProblemSetService {

    ProblemSetRevision getLatestProblemSet(long editUrl);

    ProblemSetRevision getProblemSetByShareUrl(long shareUrl);

    ProblemSetRevision saveNewVersionOfProblemSet(ProblemSet problemSet) throws IllegalArgumentException;

    ProblemSetDTO findProblemsByUrlCode(String code) throws IllegalArgumentException;

    String getDefaultProblemSetRevisionCode();
}
