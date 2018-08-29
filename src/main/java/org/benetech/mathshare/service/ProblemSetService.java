package org.benetech.mathshare.service;

import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.springframework.data.util.Pair;

public interface ProblemSetService {

    ProblemSetRevision getLatestProblemSet(long editUrl);

    ProblemSetRevision getProblemSetByShareUrl(long shareUrl);

    ProblemSetRevision saveNewProblemSet(ProblemSet problemSet) throws IllegalArgumentException;

    ProblemSetDTO findProblemsByUrlCode(String code) throws IllegalArgumentException;

    Pair<Boolean, ProblemSetRevision> createOrUpdateProblemSet(String code, ProblemSetDTO problemSet);

    ProblemSetDTO getLatestProblemSetForEditing(String code);

    String getDefaultProblemSetRevisionCode();

    String getDefaultProblemSetCode();
}
