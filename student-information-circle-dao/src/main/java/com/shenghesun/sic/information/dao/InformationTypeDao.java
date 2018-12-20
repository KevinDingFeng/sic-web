package com.shenghesun.sic.information.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.information.entity.InformationType;

@Repository
public interface InformationTypeDao extends JpaRepository<InformationType, Long>, JpaSpecificationExecutor<InformationType> {

	List<InformationType> findByRemoved(boolean bool);

	List<InformationType> findByActiveAndRemoved(boolean active, boolean removed);

	
}
