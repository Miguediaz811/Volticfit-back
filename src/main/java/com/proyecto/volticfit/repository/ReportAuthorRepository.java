package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.ReportAuthor;
import com.proyecto.volticfit.entity.ReportAuthorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportAuthorRepository extends JpaRepository<ReportAuthor, ReportAuthorId> {

    List<ReportAuthor> findByUserIdUser(Long userId);

    List<ReportAuthor> findByReportId(Integer reportId);
}
