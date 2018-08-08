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
    public ProblemSetRevision saveNewVersionOfProblemSet(ProblemSet problemSet) throws IllegalArgumentException {
        if (problemSet.getId() != null) {
            throw new IllegalArgumentException("Id must be null for the new problem set");
        }
        ProblemSetRevision newRevision = problemSetRevisionRepository.save(
                new ProblemSetRevision(problemSet));
        ProblemSetRevision oldRevision = problemSetRevisionRepository.findOneByProblemSetAndReplacedBy(problemSet, null);
        oldRevision.setReplacedBy(newRevision);
        problemSetRevisionRepository.save(oldRevision);
        return newRevision;
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
}
