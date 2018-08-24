package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionStepRepository;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public SolutionRevision getLatestSolutionRevision(long editUrl) {
        ProblemSolution problemSolution = problemSolutionRepository.findOneByEditCode(editUrl);
        return solutionRevisionRepository.findOneByProblemSolutionAndReplacedBy(problemSolution, null);
    }

    @Override
    @Transactional(readOnly = true)
    public SolutionRevision getSolutionRevisionByShareUrl(long shareUrl) {
        return solutionRevisionRepository.findOneByShareCode(shareUrl);
    }

    @Override
    public SolutionRevision saveNewVersionOfSolution(SolutionDTO solution) {
        ProblemSetRevision problemSetRevision = problemSetRevisionRepository.findOneByShareCode(
                UrlCodeConverter.fromUrlCode(solution.getProblem().getProblemSetRevisionShareCode()));
        Problem problem = problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(
                solution.getProblem().getTitle(), solution.getProblem().getText(), problemSetRevision);
        ProblemSolution problemSolution = problemSolutionRepository.save(new ProblemSolution(problem));
        em.refresh(problemSolution);

        List<SolutionStep> steps = solution.getSteps()
                .stream().map(SolutionMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        return saveNewVersionOfSolution(problemSolution, steps);
    }

    @Override
    public SolutionDTO findSolutionByUrlCode(String code) throws IllegalArgumentException {
        SolutionRevision revision = solutionRevisionRepository.findOneByShareCode(
                UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        ProblemDTO problem = ProblemMapper.INSTANCE.toDto(
                problemRepository.findById(revision.getProblemSolution().getProblem().getId()).get());

        List<SolutionStepDTO> steps = solutionStepRepository.findAllBySolutionRevision(revision)
                .stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());

        return new SolutionDTO(problem, steps, UrlCodeConverter.toUrlCode(revision.getProblemSolution().getEditCode()));
    }

    @Override
    public Pair<Boolean, SolutionRevision> createOrUpdateProblemSolution(SolutionDTO solutionDTO) {
        ProblemSolution solution = SolutionMapper.INSTANCE.fromDto(solutionDTO);
        ProblemSolution fromDB = problemSolutionRepository.findOneByEditCode(solution.getEditCode());
        if (fromDB == null) {
            Problem problem = problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(
                    solution.getProblem().getTitle(),
                    solution.getProblem().getProblemText(), problemSetRevisionRepository.findOneByShareCode(
                            UrlCodeConverter.fromUrlCode(solutionDTO.getProblem().getProblemSetRevisionShareCode())));
            fromDB = problemSolutionRepository.save(new ProblemSolution(problem));
        }
        List<SolutionStep> steps = solutionDTO.getSteps()
                .stream().map(SolutionMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        SolutionRevision newRevision = saveNewVersionOfSolution(fromDB, steps);
        boolean newSolution = problemSolutionRepository.findOneByEditCode(SolutionMapper.INSTANCE
                .fromDto(solutionDTO).getEditCode()) == null;
        em.refresh(newRevision);
        return Pair.of(newSolution, newRevision);
    }

    @Override
    public SolutionDTO getLatestProblemSolutionForEditing(String code) {
        SolutionRevision revision = getLatestSolutionRevision(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        List<SolutionStepDTO> steps = solutionStepRepository.findAllBySolutionRevision(revision)
                .stream().map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());
        return new SolutionDTO(ProblemMapper.INSTANCE.toDto(revision.getProblemSolution().getProblem()),
                steps, UrlCodeConverter.toUrlCode(revision.getProblemSolution().getEditCode()));
    }

    private SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution, List<SolutionStep> steps) {
        SolutionRevision oldRevision = solutionRevisionRepository
                .findOneByProblemSolutionAndReplacedBy(problemSolution, null);
        SolutionRevision newRevision = solutionRevisionRepository.save(
                new SolutionRevision(problemSolution));
        steps.forEach(s -> s.setSolutionRevision(newRevision));
        solutionStepRepository.saveAll(steps);
        em.refresh(newRevision);
        if (oldRevision != null) {
            oldRevision.setReplacedBy(newRevision);
            solutionRevisionRepository.save(oldRevision);
        }
        return newRevision;
    }
}
