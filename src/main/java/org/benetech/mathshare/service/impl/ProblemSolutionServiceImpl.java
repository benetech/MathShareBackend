package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionStepRepository;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemSolutionServiceImpl implements ProblemSolutionService {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolutionStepRepository solutionStepRepository;

    @Override
    @Transactional(readOnly = true)
    public SolutionRevision getLatestSolutionRevision(long editUrl) {
        ProblemSolution problemSolution = problemSolutionRepository.findOneByEditCode(editUrl);
        return solutionRevisionRepository.findAllByProblemSolutionAndReplacedBy(problemSolution, null);
    }

    @Override
    @Transactional(readOnly = true)
    public SolutionRevision getSolutionRevisionByShareUrl(long shareUrl) {
        return solutionRevisionRepository.findOneByShareCode(shareUrl);
    }

    @Override
    public SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution) {
        SolutionRevision newRevision = solutionRevisionRepository.save(
                new SolutionRevision(problemSolution));
        SolutionRevision oldRevision = solutionRevisionRepository
                .findAllByProblemSolutionAndReplacedBy(problemSolution, null);
        oldRevision.setReplacedBy(newRevision);
        solutionRevisionRepository.save(oldRevision);
        return newRevision;
    }

    @Override
    public SolutionDTO findSolutionByUrlCode(String code) throws IllegalArgumentException {
        SolutionRevision revision = solutionRevisionRepository.findOneByShareCode(
                UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        ProblemDTO problem = Arrays.asList(problemRepository.findById(revision.getProblemSolution().getId()).get())
                .stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList()).get(0);

        List<SolutionStepDTO> steps = solutionStepRepository.findAllBySolutionRevision(revision)
                .stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());

        return new SolutionDTO(problem, steps, UrlCodeConverter.toUrlCode(revision.getProblemSolution().getEditCode()));
    }
}
