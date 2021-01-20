package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.MapperUtils;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.mappers.SolutionMapper;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
import org.benetech.mathshare.model.dto.SolutionSetPublicDTO;
import org.benetech.mathshare.model.dto.SolutionDTO;
import org.benetech.mathshare.model.dto.SolutionSetDTO;
import org.benetech.mathshare.model.dto.SolutionStepDTO;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.SolutionPublicDTO;
import org.benetech.mathshare.model.entity.Problem;
import org.benetech.mathshare.model.entity.ProblemSetRevisionSolution;
import org.benetech.mathshare.model.entity.ProblemSolution;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.benetech.mathshare.model.entity.ReviewSolutionRevision;
import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.benetech.mathshare.repository.ProblemRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionRepository;
import org.benetech.mathshare.repository.ProblemSetRevisionSolutionRepository;
import org.benetech.mathshare.repository.ProblemSolutionRepository;
import org.benetech.mathshare.repository.ReviewSolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionRevisionRepository;
import org.benetech.mathshare.repository.SolutionStepRepository;
import org.benetech.mathshare.service.ProblemSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemSolutionServiceImpl implements ProblemSolutionService {

    @Autowired
    private ProblemSolutionRepository problemSolutionRepository;

    @Autowired
    private SolutionRevisionRepository solutionRevisionRepository;

    @Autowired
    private ReviewSolutionRevisionRepository reviewSolutionRevisionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SolutionStepRepository solutionStepRepository;

    @Autowired
    private ProblemSetRevisionRepository problemSetRevisionRepository;

    @Autowired
    private ProblemSetRevisionSolutionRepository problemSetRevisionSolutionRepository;

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

        List<SolutionStep> steps = solution.getSteps().stream().map(SolutionMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        return saveNewVersionOfSolution(problemSolution, steps, solution.getFinished(), solution.getEditorPosition());
    }

    @Override
    public SolutionRevision saveNewVersionOfSolutionWithExistingEditCode(SolutionDTO solution) {
        String editCode = solution.getEditCode();
        ProblemSolution problemSolution = problemSolutionRepository
                .findOneByEditCode(UrlCodeConverter.fromUrlCode(editCode));
        List<SolutionStep> steps = solution.getSteps().stream().map(SolutionMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        return saveNewVersionOfSolution(problemSolution, steps, solution.getFinished(), solution.getEditorPosition());
    }

    @Override
    public SolutionDTO findSolutionByUrlCode(String code) throws IllegalArgumentException {
        SolutionRevision revision = solutionRevisionRepository.findOneByShareCode(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        ReviewSolutionRevision rsr = reviewSolutionRevisionRepository.findOneBySolutionRevision(revision);

        ProblemDTO problem = ProblemMapper.INSTANCE
                .toDto(problemRepository.findById(revision.getProblemSolution().getProblem().getId()).get());

        List<SolutionStepDTO> steps = solutionStepRepository.findAllBySolutionRevision(revision).stream()
                .map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());

        ProblemSetRevision psr = revision.getProblemSolution().getProblem().getProblemSetRevision();

        return new SolutionDTO(problem, steps, UrlCodeConverter.toUrlCode(revision.getProblemSolution().getEditCode()),
                psr.getPalettes(), revision.getFinished(), UrlCodeConverter.toUrlCode(rsr.getReviewCode()),
                psr.isOptionalExplanations(), psr.isHideSteps(), revision.getEditorPosition());
    }

    @Override
    public Pair<Boolean, SolutionRevision> createOrUpdateProblemSolution(String code, SolutionDTO solutionDTO) {
        ProblemSolution solution = SolutionMapper.INSTANCE.fromDto(solutionDTO);
        ProblemSolution fromDB = problemSolutionRepository.findOneByEditCode(UrlCodeConverter.fromUrlCode(code));
        if (fromDB == null) {
            Problem problem = problemRepository.findOneByTitleAndProblemTextAndProblemSetRevision(
                    solution.getProblem().getTitle(), solution.getProblem().getProblemText(),
                    problemSetRevisionRepository.findOneByShareCode(
                            UrlCodeConverter.fromUrlCode(solutionDTO.getProblem().getProblemSetRevisionShareCode())));
            fromDB = problemSolutionRepository.save(new ProblemSolution(problem));
        }
        List<SolutionStep> steps = solutionDTO.getSteps().stream().map(SolutionMapper.INSTANCE::fromDto)
                .collect(Collectors.toList());
        SolutionRevision newRevision = saveNewVersionOfSolution(fromDB, steps, solutionDTO.getFinished(),
                                                                solutionDTO.getEditorPosition());
        Long editCode = SolutionMapper.INSTANCE.fromDto(solutionDTO).getEditCode();
        boolean newSolution = false;
        if (editCode != null) {
            newSolution = problemSolutionRepository.findOneByEditCode(editCode) == null;
        }
        em.refresh(newRevision);
        return Pair.of(newSolution, newRevision);
    }

    @Override
    public SolutionDTO getLatestProblemSolutionForEditing(String code) {
        SolutionRevision revision = getLatestSolutionRevision(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        String solutionEditCode = "";
        String reviewCode = null;
        ReviewSolutionRevision reviewSolutionRevision = reviewSolutionRevisionRepository
                .findOneBySolutionRevision(revision);
        if (reviewSolutionRevision != null) {
            ProblemSetRevisionSolution problemSetRevisionSolution = reviewSolutionRevision
                    .getProblemSetRevisionSolution();
            if (problemSetRevisionSolution != null) {
                solutionEditCode = UrlCodeConverter.toUrlCode(problemSetRevisionSolution.getEditCode());
            }
            reviewCode = UrlCodeConverter.toUrlCode(reviewSolutionRevision.getReviewCode());
        }

        List<SolutionStepDTO> steps = solutionStepRepository.findAllBySolutionRevision(revision).stream()
                .map(SolutionMapper.INSTANCE::toDto).collect(Collectors.toList());
        ProblemSetRevision psr = revision.getProblemSolution().getProblem().getProblemSetRevision();
        SolutionDTO solutionDTO = new SolutionDTO(
                ProblemMapper.INSTANCE.toDto(revision.getProblemSolution().getProblem()), steps,
                UrlCodeConverter.toUrlCode(revision.getProblemSolution().getEditCode()), psr.getPalettes(),
                revision.getFinished(), reviewCode,
                psr.isOptionalExplanations(), psr.isHideSteps(), revision.getEditorPosition());
        solutionDTO.setProblemSetSolutionEditCode(solutionEditCode);
        return solutionDTO;
    }

    @Override
    public SolutionSetDTO setArchiveMode(String code, String initiator, String role, String archiveMode) {
        ProblemSetRevisionSolution current = problemSetRevisionSolutionRepository.findOneByEditCode(
                UrlCodeConverter.fromUrlCode(code));
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
        return getSolutionSetDTOfromProblemSetRevisionSolution(
            problemSetRevisionSolutionRepository.save(current)
        );
    }

    private SolutionSetDTO createOrUpdateReviewSolutions(List<SolutionDTO> solutionsDTO,
            ProblemSetRevisionSolution problemSetRevisionSolution, boolean isCreate) {
        List<SolutionDTO> savedSolutions = new ArrayList<>();
        Long reviewCode = MapperUtils.nextCode(em);
        SolutionSetDTO solutionSet = new SolutionSetDTO();
        solutionSet.setId(problemSetRevisionSolution.getId());
        solutionSet.setSource(problemSetRevisionSolution.getSource());
        solutionSet.setReviewCode(MapperUtils.toCode(reviewCode));
        solutionSet.setArchiveMode(problemSetRevisionSolution.getArchiveMode());
        solutionSet.setArchiveModeProblemSet(
            problemSetRevisionSolution.getProblemSetRevision().getProblemSet().getArchiveMode()
        );

        String title = null;
        for (SolutionDTO solutionDTO: solutionsDTO) {
            SolutionRevision solutionRevision;
            if (isCreate) {
                solutionRevision = saveNewVersionOfSolution(solutionDTO);
            } else {
                solutionRevision = saveNewVersionOfSolutionWithExistingEditCode(solutionDTO);
            }
            solutionRevisionRepository.save(solutionRevision);
            em.refresh(solutionRevision);

            if (title == null) {
                ProblemDTO problem = solutionDTO.getProblem();
                ProblemSetRevision problemSet = problemSetRevisionRepository
                        .findOneByShareCode(MapperUtils.fromCode(problem.getProblemSetRevisionShareCode()));
                ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toProblemSetDTO((problemSet));
                if (problemSet != null) {
                    title = problemSetDTO.getTitle();
                }
            }

            ProblemSolution problemSolution = solutionRevision.getProblemSolution();
            solutionDTO.setShareCode(UrlCodeConverter.toUrlCode(solutionRevision.getShareCode()));
            solutionDTO.setEditCode(UrlCodeConverter.toUrlCode(problemSolution.getEditCode()));
            ReviewSolutionRevision reviewSolutionRevision = new ReviewSolutionRevision(solutionRevision, reviewCode,
                    problemSetRevisionSolution);
            reviewSolutionRevisionRepository.save(reviewSolutionRevision);
            em.refresh(reviewSolutionRevision);

            savedSolutions.add(solutionDTO);
        }
        solutionSet.setTitle(title);
        solutionSet.setSolutions(savedSolutions);
        return solutionSet;
    }

    @Override
    public SolutionSetDTO createReviewSolutionsFromShareCode(String code, Map<String, String> searchParameters,
            String initiator, String source) {
        ProblemSetRevision revision = problemSetRevisionRepository
                .findOneByShareCode(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        String json = null;
        try {
            for (String getParamKey : searchParameters.keySet()) {
                if (!Arrays.asList("studyPlanResourceId", "endpoint", "version").contains(getParamKey)) {
                    searchParameters.remove(getParamKey);
                }
            }
            json = new ObjectMapper().writeValueAsString(searchParameters);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<SolutionDTO> solutionsDTO = new ArrayList<>();

        for (Problem problem:revision.getProblems()) {
            SolutionDTO solutionDTO = new SolutionDTO();
            solutionDTO.setProblem(ProblemMapper.INSTANCE.toDto(problem));
            List<SolutionStepDTO> solutionSteps = new ArrayList<>();
            solutionSteps.add(new SolutionStepDTO(problem.getTitle(), problem.getProblemText(), false, true, null, null));
            solutionDTO.setSteps(solutionSteps);
            solutionsDTO.add(solutionDTO);
        }

        Long editCode = MapperUtils.nextCode(em);
        ProblemSetRevisionSolution problemSetRevisionSolution = new ProblemSetRevisionSolution(
            revision, editCode, initiator, json
        );
        problemSetRevisionSolution.setSource(source);
        problemSetRevisionSolutionRepository.save(problemSetRevisionSolution);
        em.refresh(problemSetRevisionSolution);

        SolutionSetDTO solutionSetDTO = createOrUpdateReviewSolutions(solutionsDTO, problemSetRevisionSolution, true);
        solutionSetDTO.setEditCode(MapperUtils.toCode(editCode));
        solutionSetDTO.setArchiveMode(problemSetRevisionSolution.getArchiveMode());
        solutionSetDTO.setArchiveModeProblemSet(revision.getProblemSet().getArchiveMode());
        solutionSetDTO.deserializeAndSetMetadata(json);
        return solutionSetDTO;
    }

    @Override
    public SolutionSetDTO createReviewSolutions(String code, List<SolutionDTO> solutionsDTO, String initiator) {
        ProblemSetRevision revision = problemSetRevisionRepository.findOneByShareCode(
                UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }

        Long editCode = MapperUtils.nextCode(em);
        ProblemSetRevisionSolution problemSetRevisionSolution = new ProblemSetRevisionSolution(
                revision, editCode, initiator
        );
        problemSetRevisionSolutionRepository.save(problemSetRevisionSolution);
        em.refresh(problemSetRevisionSolution);

        SolutionSetDTO solutionSetDTO = createOrUpdateReviewSolutions(solutionsDTO, problemSetRevisionSolution, true);
        solutionSetDTO.setEditCode(MapperUtils.toCode(editCode));
        solutionSetDTO.setArchiveMode(problemSetRevisionSolution.getArchiveMode());
        solutionSetDTO.setArchiveModeProblemSet(revision.getProblemSet().getArchiveMode());
        return solutionSetDTO;
    }

    @Override
    public SolutionSetDTO updateReviewSolutions(String editCode, List<SolutionDTO> solutionsDTO) {
        ProblemSetRevisionSolution problemSetRevisionSolution = problemSetRevisionSolutionRepository.findOneByEditCode(
                UrlCodeConverter.fromUrlCode(editCode));
        if (problemSetRevisionSolution == null) {
            return null;
        }
        reviewSolutionRevisionRepository.setAllReviewSolutionRevisionsInactiveFor(problemSetRevisionSolution);
        SolutionSetDTO solutionSetDTO = createOrUpdateReviewSolutions(solutionsDTO, problemSetRevisionSolution, false);
        solutionSetDTO.setEditCode(editCode);
        solutionSetDTO.setSource(problemSetRevisionSolution.getSource());
        solutionSetDTO.setArchiveMode(problemSetRevisionSolution.getArchiveMode());
        solutionSetDTO.setArchiveModeProblemSet(
            problemSetRevisionSolution.getProblemSetRevision().getProblemSet().getArchiveMode()
        );
        return solutionSetDTO;
    }

    @Override
    public SolutionSetPublicDTO getReviewSolutions(String reviewCode) {
        SolutionSetPublicDTO solutionSet = new SolutionSetPublicDTO();
        solutionSet.setReviewCode(reviewCode);
        List<SolutionPublicDTO> solutionsDTO = new ArrayList<>();
        List<ReviewSolutionRevision> reviewSolutionRevisions = reviewSolutionRevisionRepository
                .findAllByReviewCode(MapperUtils.fromCode(reviewCode));
        String title = null;
        for (ReviewSolutionRevision reviewSolutionRevision:reviewSolutionRevisions) {
            SolutionRevision solutionRevision = reviewSolutionRevision.getSolutionRevision();
            ProblemSolution solution = solutionRevision.getProblemSolution();
            SolutionPublicDTO solutionDTO = SolutionMapper.INSTANCE.toReadonlyDto(solution);
            if (title == null) {
                ProblemDTO problem = solutionDTO.getProblem();
                ProblemSetRevision problemSet =  problemSetRevisionRepository.findOneByShareCode(
                        MapperUtils.fromCode(problem.getProblemSetRevisionShareCode())
                );
                ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toProblemSetDTO((problemSet));
                if (problemSet != null) {
                    title = problemSetDTO.getTitle();
                }
            }

            solutionDTO.setShareCode(UrlCodeConverter.toUrlCode((solutionRevision.getShareCode())));
            solutionsDTO.add(solutionDTO);
        }
        solutionSet.setSolutions(solutionsDTO);
        solutionSet.setTitle(title);
        return solutionSet;
    }

    @Override
    public SolutionSetDTO getProblemSetSolutions(String editCode) {
        return getSolutionSetDTOfromProblemSetRevisionSolution(
            problemSetRevisionSolutionRepository.findOneByEditCode(MapperUtils.fromCode(editCode))
        );
    }

    @Override
    public List<SolutionSetDTO> getProblemSetSolutionsForUsers(String userId, String archiveMode, Integer n) {
        List<SolutionSetDTO> solutionSets = new ArrayList<>();
        List<ProblemSetRevisionSolution> problemSetRevisions = problemSetRevisionSolutionRepository
            .findAllByUserIdAndArchiveMode(
                userId,
                archiveMode,
                PageRequest.of(0, n, Sort.by("id").descending())
            );
        for (ProblemSetRevisionSolution problemSetRevision : problemSetRevisions) {
            solutionSets.add(getSolutionSetDTOfromProblemSetRevisionSolution(problemSetRevision));
        }
        return solutionSets;
    }

    @Override
    public List<SolutionSetDTO> getLatestProblemSetSolutionsForUsers(String userId, String archiveMode, Integer size,
            Integer id) {
        List<SolutionSetDTO> solutionSets = new ArrayList<>();
        List<ProblemSetRevisionSolution> problemSetRevisions = problemSetRevisionSolutionRepository
                .findLatestSliceByUserIdAndArchiveMode(userId, archiveMode, id,
                        PageRequest.of(0, size, Sort.by("id").descending()));
        for (ProblemSetRevisionSolution problemSetRevision : problemSetRevisions) {
            solutionSets.add(getSolutionSetDTOfromProblemSetRevisionSolution(problemSetRevision));
        }
        return solutionSets;
    }

    @Override
    public Integer getRemainingCountOfLatestProblemSetSolutionsForUsers(String userId, String archiveMode, Integer id) {
        return problemSetRevisionSolutionRepository.findRemainingCountByUserIdAndArchiveMode(userId, archiveMode, id);
    }

    private SolutionSetDTO getSolutionSetDTOfromProblemSetRevisionSolution(
        ProblemSetRevisionSolution problemSetRevisionSolution
    ) {
        SolutionSetDTO solutionSet = new SolutionSetDTO();
        List<SolutionDTO> solutionsDTO = new ArrayList<>();
        List<ReviewSolutionRevision> reviewSolutionRevisions = reviewSolutionRevisionRepository
                .findAllByProblemSetRevisionSolutionAndInactive(problemSetRevisionSolution, false);
        String title = null;
        String reviewCode = null;
        for (ReviewSolutionRevision reviewSolutionRevision:reviewSolutionRevisions) {
            SolutionRevision solutionRevision = reviewSolutionRevision.getSolutionRevision();
            SolutionDTO solutionDTO = SolutionMapper.INSTANCE.toSolutionDTOWithSteps(solutionRevision);
            if (title == null) {
                ProblemDTO problem = solutionDTO.getProblem();
                ProblemSetRevision problemSet =  problemSetRevisionRepository.findOneByShareCode(
                        MapperUtils.fromCode(problem.getProblemSetRevisionShareCode())
                );
                ProblemSetDTO problemSetDTO = ProblemMapper.INSTANCE.toProblemSetDTO((problemSet));
                if (problemSet != null) {
                    title = problemSetDTO.getTitle();
                }
            }

            if (reviewCode == null) {
                reviewCode = MapperUtils.toCode(reviewSolutionRevision.getReviewCode());
            }

            solutionDTO.setShareCode(UrlCodeConverter.toUrlCode((solutionRevision.getShareCode())));
            solutionsDTO.add(solutionDTO);
        }
        solutionSet.setSolutions(solutionsDTO);
        solutionSet.setTitle(title);
        solutionSet.setReviewCode(reviewCode);
        solutionSet.setEditCode(MapperUtils.toCode(problemSetRevisionSolution.getEditCode()));
        solutionSet.setId(problemSetRevisionSolution.getId());
        solutionSet.setSource(problemSetRevisionSolution.getSource());
        solutionSet.setArchiveMode(problemSetRevisionSolution.getArchiveMode());
        solutionSet.setArchiveModeProblemSet(
            problemSetRevisionSolution.getProblemSetRevision().getProblemSet().getArchiveMode()
        );
        return solutionSet;
    }

    private SolutionRevision saveNewVersionOfSolution(ProblemSolution problemSolution, List<SolutionStep> steps,
            boolean finished, Integer editorPosition) {
        SolutionRevision oldRevision = solutionRevisionRepository
                .findOneByProblemSolutionAndReplacedBy(problemSolution, null);
        SolutionRevision newRevision = solutionRevisionRepository.save(
                new SolutionRevision(problemSolution, finished, editorPosition));
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
