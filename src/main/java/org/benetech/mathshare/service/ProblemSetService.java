package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProblemSetService {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Transactional(readOnly = true)
    public ProblemSetRevision getLatestProblemSet(long editUrl) {
        ProblemSet problemSet = problemSetRepository.findOneByEditCode(editUrl);
        return problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null);
    }

    @Transactional(readOnly = true)
    public ProblemSetRevision getProblemSetByShareUrl(long shareUrl) {
        return problemSetRevisionRepository.findOneByShareCode(shareUrl);
    }

    @Transactional
    public ProblemSetRevision saveNewVersionOfProblemSet(ProblemSet problemSet) {
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(
                new ProblemSetRevision(problemSet));
        ProblemSetRevision oldRevision = problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null);
        oldRevision.setReplacedBy(newRevision);
        problemSetRevisionRepository.save(oldRevision);
        return newRevision;
    }
}
