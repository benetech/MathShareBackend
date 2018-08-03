package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSetRevisionRepository extends JpaRepository<ProblemSetRevision, Long> {

    ProblemSetRevision findByShareCode(String shareCode);
    ProblemSetRevision findByProblemSetAndReplacedBy(ProblemSet problemSet, ProblemSetRevision replacedBy);
}
