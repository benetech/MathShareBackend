package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRevisionRepository extends JpaRepository<SolutionRevision, Long> {
}
