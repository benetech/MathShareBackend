package org.benetech.mathshare.repository;

import org.benetech.mathshare.model.entity.Scratchpad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScratchpadRepository extends JpaRepository<Scratchpad, Integer> {
}
