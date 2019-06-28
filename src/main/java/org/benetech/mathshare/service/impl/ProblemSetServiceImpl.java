package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.service.ProblemSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemSetServiceImpl implements ProblemSetService {

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRepository problemSetRepository;

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
    public ProblemSetRevision saveNewProblemSet(ProblemSetDTO problemSetDTO) throws IllegalArgumentException {
        ProblemSet problemSet = ProblemMapper.INSTANCE.fromDto(problemSetDTO);
        List<Problem> problems = problemSetDTO.getProblems().stream().map(ProblemMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        List<Problem> savedProblems = new ArrayList<>();

        ProblemSet set = problemSetRepository.save(problemSet);
        em.refresh(set);
        ProblemSetRevision revision = problemSetRevisionRepository.save(
                new ProblemSetRevision(set, problemSetDTO.getTitle())
        );
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision));
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
    @Transactional(readOnly = true)
    public ProblemSetDTO findProblemsByUrlCode(String code) throws IllegalArgumentException {
        ProblemSetRevision revision = problemSetRevisionRepository.findOneByShareCode(
                UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        List<ProblemDTO> problems = problemRepository.findAllByProblemSetRevision(revision)
                .stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        return new ProblemSetDTO(problems, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()),
                UrlCodeConverter.toUrlCode(revision.getShareCode()), revision.getProblemSet().getPalettes(),
                revision.getTitle());
    }

    @Override
    public Pair<Boolean, ProblemSetRevision> createOrUpdateProblemSet(String code, ProblemSetDTO problemSetDTO) {
        ProblemSet problemSet = ProblemMapper.INSTANCE.fromDto(problemSetDTO);
        ProblemSet saved = problemSetRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(code));
        List<Problem> problems = problemSetDTO.getProblems().stream().map(ProblemMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        List<Problem> savedProblems = new ArrayList<>();
        ProblemSetRevision result;
        boolean newSet;
        String title = problemSetDTO.getTitle();
        if (saved == null) {
            result = createProblemSet(savedProblems, problems, problemSet, title);
            newSet = true;
        } else {
            result = updateProblemSet(savedProblems, problems, problemSet, title);
            newSet = false;
        }
        em.refresh(result);
        return Pair.of(newSet, result);
    }

    private ProblemSetRevision createProblemSet(List<Problem> savedProblems, List<Problem> problems,
                                                ProblemSet problemSet, String title) {
        ProblemSet set = problemSetRepository.save(problemSet);
        ProblemSetRevision revision = problemSetRevisionRepository.save(new ProblemSetRevision(set, title));
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision));
        }
        revision.setProblems(savedProblems);
        return problemSetRevisionRepository.save(revision);
    }

    private ProblemSetRevision updateProblemSet(List<Problem> savedProblems, List<Problem> problems,
                                                ProblemSet problemSet, String title) {
        ProblemSetRevision result;
        ProblemSet set = problemSetRepository.findOneByEditCode(problemSet.getEditCode());
        ProblemSetRevision oldRevision = problemSetRevisionRepository
                .findOneByProblemSetAndReplacedBy(set, null);
        ProblemSetRevision revision = problemSetRevisionRepository.save(new ProblemSetRevision(set, title));
        for (Problem problem : problems) {
            savedProblems.add(createOrUpdateProblem(problem, revision));
        }
        revision.setProblems(savedProblems);
        oldRevision.setProblems(this.problemRepository.findAllByProblemSetRevision(oldRevision));
        result = problemSetRevisionRepository.save(revision);
        oldRevision.setReplacedBy(result);
        problemSetRevisionRepository.save(oldRevision);
        return result;
    }

    private Problem createOrUpdateProblem(Problem problem, ProblemSetRevision problemSetRevision) {
        Problem saved = this.problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(problem.getTitle(),
                problem.getProblemText(), problemSetRevision);
        if (saved == null) {
            problem.setProblemSetRevision(problemSetRevision);
            problem.setId(null);
            return problemRepository.save(problem);
        } else {
            Problem newVersion = new Problem(saved.getProblemText(), saved.getTitle(), problemSetRevision);
            newVersion = problemRepository.save(newVersion);
            saved.setReplacedBy(newVersion);
            problemRepository.save(saved);
            return newVersion;
        }
    }

    @Override
    public ProblemSetDTO getLatestProblemSetForEditing(String code) {
        ProblemSetRevision revision = this.getLatestProblemSet(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        List<ProblemDTO> problems = problemRepository.findAllByProblemSetRevision(revision)
                .stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        return new ProblemSetDTO(problems, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()),
                UrlCodeConverter.toUrlCode(revision.getShareCode()), revision.getProblemSet().getPalettes(),
                revision.getTitle());
    }
}
