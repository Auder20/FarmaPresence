package com.farmapresence.repository;

import com.farmapresence.models.InformeAsistencia_Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformeAsistencia_Repository extends JpaRepository<InformeAsistencia_Model, Long> {
}
