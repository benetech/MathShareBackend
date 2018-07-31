package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.SolutionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionStepRepository extends JpaRepository<SolutionStep, Long> {
}
