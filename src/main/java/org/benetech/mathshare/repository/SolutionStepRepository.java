package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionRevision;
import org.benetech.mathshare.model.entity.SolutionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionStepRepository extends JpaRepository<SolutionStep, Integer> {

    List<SolutionStep> findAllBySolutionRevision(SolutionRevision solutionRevision);

}
