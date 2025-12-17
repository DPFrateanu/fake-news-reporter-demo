package com.automatica.fakenews.repository;

import com.automatica.fakenews.model.FakeNewsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FakeNewsReportRepository extends JpaRepository<FakeNewsReport, Long> {
    
    List<FakeNewsReport> findByStatusOrderByReportedAtDesc(com.automatica.fakenews.model.Status status);
    
    List<FakeNewsReport> findAllByOrderByReportedAtDesc();
}
