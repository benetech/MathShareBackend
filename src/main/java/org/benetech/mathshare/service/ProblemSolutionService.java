package org.benetech.mathshare.service;

import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProblemSolutionService {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Transactional(readOnly = true)
    public SolutionRevision getLatestSolutionRevision(long editUrl) {
        ProblemSolution problemSolution = problemSolutionRepository.findOneByEditCode(editUrl);
        return solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(problemSolution, null);
    }

    @Transactional(readOnly = true)
    public SolutionRevision getSolutionRevisionByShareUrl(long shareUrl) {
        return solutionRevisionRepository.findOneByShareCode(shareUrl);
    }

    @Transactional
    public SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution) {
        SolutionRevision newRevision = solutionRevisionRepository.save(
                new SolutionRevision(problemSolution));
        SolutionRevision oldRevision = solutionRevisionRepository
                .findAllByProblemSolutionAndReplacedBy(problemSolution, null);
        oldRevision.setReplacedBy(newRevision);
        return solutionRevisionRepository.save(oldRevision);
    }
}
