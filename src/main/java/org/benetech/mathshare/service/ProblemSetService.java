package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemSetService {

    private static final String GENERATED_URL = "generatedUrl";

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    public ProblemSetRevision getLatestProblemSet(String editUrl) {
        ProblemSet problemSet = problemSetRepository.findOneByEditCode(editUrl);
        return problemSetRevisionRepository.findAllByProblemSetAndReplacedBy(problemSet, null);
    }

    public ProblemSetRevision getProblemSetByShareUrl(String shareUrl) {
        return problemSetRevisionRepository.findOneByShareCode(shareUrl);
    }

    public void saveNewVersionOfProblemSet(ProblemSet problemSet) {
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(
                new ProblemSetRevision(problemSet, GENERATED_URL));
        ProblemSetRevision oldRevision = problemSetRevisionRepository.findAllByProblemSetAndReplacedBy(problemSet, null);
        oldRevision.setReplacedBy(newRevision);
        problemSetRevisionRepository.save(oldRevision);
    }
}
