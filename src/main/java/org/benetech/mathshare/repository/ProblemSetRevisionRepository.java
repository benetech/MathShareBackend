package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.ProblemSet;
import org.benetech.mathshare.model.entity.ProblemSetRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemSetRevisionRepository extends JpaRepository<ProblemSetRevision, Integer> {

    ProblemSetRevision findOneByProblemSetAndReplacedBy(ProblemSet problemSet, ProblemSetRevision replacedBy);

    ProblemSetRevision findOneByShareCode(long code);

    ProblemSetRevision findFirstByOrderByDateCreatedAsc();
}
