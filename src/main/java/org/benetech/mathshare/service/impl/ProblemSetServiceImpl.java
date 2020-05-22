package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.dto.ProblemStepDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ProblemStep;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.repository.ProblemStepRepository;
import org.benetech.mathshare.service.ProblemSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemSetServiceImpl implements ProblemSetService {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

    @Autowired
    private ProblemStepRepository problemStepRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public ProblemSetRevision getLatestProblemSet(long editUrl) {
        ProblemSet problemSet = problemSetRepository.findOneByEditCode(editUrl);
        return problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemSetRevision getProblemSetByShareUrl(long shareUrl) {
        return problemSetRevisionRepository.findOneByShareCode(shareUrl);
    }

    @Override
    public ProblemSetRevision saveNewProblemSet(
            ProblemSetDTO problemSetDTO, String initiator
    ) throws IllegalArgumentException {
        problemSetDTO.setUserId(initiator);
        ProblemSet problemSet = ProblemMapper.INSTANCE.fromDto(problemSetDTO);
        List<Problem> problems = problemSetDTO.getProblems().stream().map(ProblemMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        List<Problem> savedProblems = new ArrayList<>();
        boolean optionalExplanations = true;
        if (problemSetDTO.getOptionalExplanations() != null) {
            optionalExplanations = problemSetDTO.getOptionalExplanations().booleanValue();
        }
        boolean hideSteps = false;
        if (problemSetDTO.getHideSteps() != null) {
            hideSteps = problemSetDTO.getHideSteps().booleanValue();
        }
        List<String> palettes = problemSet.getPalettes();
        if (palettes == null) {
            palettes = Arrays.asList("Edit;Operators;Notations;Geometry".split(";"));
        }
        ProblemSet set = problemSetRepository.save(problemSet);
        em.refresh(set);
        ProblemSetRevision revision = problemSetRevisionRepository
                .save(new ProblemSetRevision(
                    set,
                    problemSetDTO.getTitle(),
                    optionalExplanations,
                    hideSteps,
                    palettes
                ));
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision, problem.getSteps()));
        }
        revision.setProblems(savedProblems);
        em.refresh(revision);
        return problemSetRevisionRepository.save(revision);
    }

    @Override
    public String getDefaultProblemSetRevisionCode() {
        ProblemSetRevision revision = problemSetRevisionRepository.findFirstByOrderByDateCreatedAsc();
        return revision == null ? null : UrlCodeConverter.toUrlCode(revision.getShareCode());
    }

    @Override
    public String getDefaultProblemSetCode() {
        ProblemSet set = problemSetRevisionRepository.findFirstByOrderByDateCreatedAsc().getProblemSet();
        return set == null ? null : UrlCodeConverter.toUrlCode(set.getEditCode());
    }

    @Override
    public List<ProblemSetDTO> findAllExampleProblems() {
        List<ProblemSetRevision> problemSets = problemSetRevisionRepository.findAllByIsExample(true);
        return problemSets.stream().map(ProblemMapper.INSTANCE::toProblemSetDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProblemSetDTO> findLastNProblemSetsOfUser(String userId, String archiveMode, int n) {
        List<ProblemSet> problemSets = problemSetRepository.findAllByUserIdAndArchiveMode(userId,
                archiveMode,
                PageRequest.of(0, n, Sort.by("id").descending()));
        return problemSets.stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public ProblemSetDTO setArchiveMode(String code, String initiator, String role, String archiveMode) {
        ProblemSet current = problemSetRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(code));
        if (current == null) {
            return null;
        }
        if (!"admin".equals(role)) {
            if (initiator == null) {
                return null;
            } else if (current.getUserId() == null) {
                return null;
            } else if (!initiator.equals(current.getUserId())) {
                return null;
            }
        }
        current.setArchiveMode(archiveMode);
        current.setArchivedBy(initiator);
        return ProblemMapper.INSTANCE.toDto(problemSetRepository.save(current));
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemSetDTO findProblemsByUrlCode(String code) throws IllegalArgumentException {
        ProblemSetRevision revision = problemSetRevisionRepository
                .findOneByShareCode(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        List<ProblemDTO> problemsDto = new ArrayList<>();
        List<Problem> problems = problemRepository.findAllByProblemSetRevision(revision);
        for (Problem problem : problems) {
            ProblemDTO problemDto = ProblemMapper.INSTANCE.toDto(problem);
            if (revision.isHideSteps()) {
                problemDto.setSteps(new ArrayList<>());
            } else {
                problemDto.setSteps(problemStepRepository.findAllByProblemOrderByIdAsc(problem).stream()
                        .map(ProblemMapper.INSTANCE::toStepDto).collect(Collectors.toList()));
            }
            problemsDto.add(problemDto);
        }

        return new ProblemSetDTO(problemsDto, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()),
                UrlCodeConverter.toUrlCode(revision.getShareCode()), revision.getPalettes(),
                revision.getTitle(), null, problems.size(), revision.getProblemSet().getArchiveMode(),
                revision.isOptionalExplanations(), revision.isHideSteps());
    }

    @Override
    public Pair<Boolean, ProblemSetRevision> createOrUpdateProblemSet(
            String code, ProblemSetDTO problemSetDTO, String initiator
    ) {
        problemSetDTO.setUserId(initiator);
        ProblemSet problemSet = ProblemMapper.INSTANCE.fromDto(problemSetDTO);
        ProblemSet saved = problemSetRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(code));
        List<Problem> problems = problemSetDTO.getProblems().stream().map(ProblemMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        List<Problem> savedProblems = new ArrayList<>();
        boolean optionalExplanations = true;
        if (problemSetDTO.getOptionalExplanations() != null) {
            optionalExplanations = problemSetDTO.getOptionalExplanations().booleanValue();
        }
        boolean hideSteps = false;
        if (problemSetDTO.getHideSteps() != null) {
            hideSteps = problemSetDTO.getHideSteps().booleanValue();
        }
        ProblemSetRevision result;
        boolean newSet;
        String title = problemSetDTO.getTitle();
        List<String> palettes = problemSet.getPalettes();
        if (saved == null) {
            result = createProblemSet(savedProblems, problems, problemSet, title, optionalExplanations, hideSteps, palettes);
            newSet = true;
        } else {
            if (palettes == null) {
                palettes = saved.getLatestRevision().getPalettes();
            }
            result = updateProblemSet(savedProblems, problems, problemSet, title, optionalExplanations, hideSteps, palettes);
            newSet = false;
        }
        em.refresh(result);
        return Pair.of(newSet, result);
    }

    @Override
    public ProblemSetRevision updateProblemStepsInProblemSet(String code, Integer problemId,
            List<ProblemStepDTO> problemSteps, String initiator) {
        ProblemSet problemSet = problemSetRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(code));
        List<Problem> problems = ProblemMapper.INSTANCE.toDto(problemSet).getProblems().stream()
                .map(ProblemMapper.INSTANCE::fromDto).collect(Collectors.toList());
        List<Problem> savedProblems = new ArrayList<>();
        ProblemSetRevision oldRevision = problemSet.getLatestRevision();
        ProblemSetRevision result = new ProblemSetRevision(
            problemSet,
            oldRevision.getTitle(),
            oldRevision.isOptionalExplanations(),
            oldRevision.isHideSteps(),
            oldRevision.getPalettes()
        );
        ProblemSetRevision revision = problemSetRevisionRepository.save(result);
        for (Problem problem : problems) {
            List<ProblemStep> steps = problem.getSteps();
            if (problem.getId().equals(problemId)) {
                steps = problemSteps.stream().map(ProblemMapper.INSTANCE::fromStepDto).collect(Collectors.toList());
            }
            savedProblems.add(createOrUpdateProblem(problem, revision, steps));
        }
        revision.setProblems(savedProblems);
        oldRevision.setProblems(this.problemRepository.findAllByProblemSetRevision(oldRevision));
        result = problemSetRevisionRepository.save(revision);
        em.refresh(result);
        oldRevision.setReplacedBy(result);
        problemSetRevisionRepository.save(oldRevision);
        return result;
    }

    private ProblemSetRevision createProblemSet(List<Problem> savedProblems, List<Problem> problems,
                                                ProblemSet problemSet, String title,
                                                boolean optionalExplanations, boolean hideSteps,
                                                List<String> palettes) {
        ProblemSet set = problemSetRepository.save(problemSet);
        ProblemSetRevision revision = problemSetRevisionRepository.save(
            new ProblemSetRevision(set, title, optionalExplanations, hideSteps, palettes)
        );
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision, new ArrayList<>()));
        }
        revision.setProblems(savedProblems);
        return problemSetRevisionRepository.save(revision);
    }

    private ProblemSetRevision updateProblemSet(List<Problem> savedProblems, List<Problem> problems,
                                                ProblemSet problemSet, String title,
                                                boolean optionalExplanations, boolean hideSteps,
                                                List<String> palettes) {
        ProblemSetRevision result;
        ProblemSet set = problemSetRepository.findOneByEditCode(problemSet.getEditCode());
        ProblemSetRevision oldRevision = problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(set, null);
        ProblemSetRevision revision = problemSetRevisionRepository.save(
            new ProblemSetRevision(set, title, optionalExplanations, hideSteps, palettes)
        );
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision, problem.getSteps()));
        }
        revision.setProblems(savedProblems);
        oldRevision.setProblems(this.problemRepository.findAllByProblemSetRevision(oldRevision));
        result = problemSetRevisionRepository.save(revision);
        oldRevision.setReplacedBy(result);
        problemSetRevisionRepository.save(oldRevision);
        return result;
    }

    private Problem createOrUpdateProblem(Problem problem, ProblemSetRevision problemSetRevision,
            List<ProblemStep> steps) {
        Problem saved = null;
        if (problem.getId() != null) {
            saved = this.problemRepository.findById(problem.getId()).get();
        }
        if (saved == null) {
            problem.setProblemSetRevision(problemSetRevision);
            Problem newSaved = problemRepository.save(problem);
            problemRepository.save(newSaved);
            em.refresh(newSaved);
            steps.forEach(s -> s.setProblem(newSaved));
            problemStepRepository.saveAll(steps);
            return newSaved;
        } else {
            Problem newVersion = problemRepository.save(
                new Problem(problem.getProblemText(), problem.getTitle(), problemSetRevision,
                        problem.getScratchpad())
            );
            steps.forEach(s -> s.setProblem(newVersion));
            saved.setReplacedBy(newVersion);
            problemRepository.save(saved);
            problemStepRepository.saveAll(steps);
            newVersion.setSteps(steps);
            problemRepository.save(newVersion);
            return newVersion;
        }
    }

    @Override
    public ProblemSetDTO getLatestProblemSetForEditing(String code) {
        ProblemSetRevision revision = this.getLatestProblemSet(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        List<ProblemDTO> problems = problemRepository.findAllByProblemSetRevision(revision).stream()
                .map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        return new ProblemSetDTO(problems, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()),
                UrlCodeConverter.toUrlCode(revision.getShareCode()), revision.getPalettes(),
                revision.getTitle(), null, problems.size(), revision.isOptionalExplanations(), revision.isHideSteps());
    }

    @Override
    public Integer updateIsExampleForSet(String code, boolean isExample) {
        return problemSetRevisionRepository.setIsExampleForProblemSetRevision(
            UrlCodeConverter.fromUrlCode(code), isExample);
    }
}
