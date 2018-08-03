package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemSetService {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;
    @Autowired
    private ProblemSetRepository problemSetRepository;

    public ProblemSetService() {
    }

    public ProblemSetRevision getLatestProblemSet(String editUrl) {
        ProblemSet problemSet = problemSetRepository.findByEditCode(editUrl);
        return problemSetRevisionRepository.findByProblemSetAndReplacedBy(problemSet, null);
    }

    public ProblemSetRevision getProblemSetByShareUrl(String shareUrl) {
        return problemSetRevisionRepository.findByShareCode(shareUrl);
    }


    public void saveNewVersionOfProblemSet(ProblemSet problemSet) {
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(new ProblemSetRevision(problemSet, "generatedUrl"));
        ProblemSetRevision oldRevision = problemSetRevisionRepository.findByProblemSetAndReplacedBy(problemSet, null);
        oldRevision.setReplacedBy(newRevision);
        problemSetRevisionRepository.save(oldRevision);
    }
}
