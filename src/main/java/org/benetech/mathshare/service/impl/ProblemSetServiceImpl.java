package org.benetech.mathshare.service.impl;

import org.benetech.mathshare.converters.UrlCodeConverter;
import org.benetech.mathshare.mappers.ProblemMapper;
import org.benetech.mathshare.model.dto.ProblemDTO;
import org.benetech.mathshare.model.dto.ProblemSetDTO;
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
    public ProblemSetRevision saveNewProblemSet(ProblemSet problemSet) throws IllegalArgumentException {
        if (problemSet.getId() != null) {
            throw new IllegalArgumentException("Id must be null for the new problem set");
        }
        ProblemSet savedProblemSet = problemSetRepository.save(problemSet);
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(
                new ProblemSetRevision(savedProblemSet));
        return newRevision;
    }

    @Override
    public String getDefaultProblemSetRevisionCode() {
        ProblemSetRevision revision = problemSetRevisionRepository.findFirstByOrderByDateCreatedAsc();
        return revision == null ? null : UrlCodeConverter.toUrlCode(revision.getShareCode());
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
        return new ProblemSetDTO(problems, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()));
    }

    @Override
    public Pair<Boolean, ProblemSetRevision> createOrUpdateProblemSet(ProblemSet problemSet) {
        ProblemSet saved = problemSet;
        if (problemSet.getId() == null) {
            saved = problemSetRepository.save(problemSet);
        }
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(
                new ProblemSetRevision(saved));
        boolean newSolution = true;
        if (problemSet.getId() != null) {
            ProblemSetRevision oldRevision = problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(saved, null);
            oldRevision.setReplacedBy(newRevision);
            problemSetRevisionRepository.save(oldRevision);
            newSolution = false;
        }
        return Pair.of(newSolution, newRevision);
    }

    @Override
    public ProblemSetDTO getLatestProblemSetForEditing(String code) {
        ProblemSetRevision revision = this.getLatestProblemSet(UrlCodeConverter.fromUrlCode(code));
        if (revision == null) {
            return null;
        }
        List<ProblemDTO> problems = problemRepository.findAllByProblemSetRevision(revision)
                .stream().map(ProblemMapper.INSTANCE::toDto).collect(Collectors.toList());
        return new ProblemSetDTO(problems, UrlCodeConverter.toUrlCode(revision.getProblemSet().getEditCode()));
    }
}
