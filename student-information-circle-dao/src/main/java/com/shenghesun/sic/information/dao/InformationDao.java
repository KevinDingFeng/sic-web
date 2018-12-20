package com.shenghesun.sic.information.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.information.entity.Information;

@Repository
public interface InformationDao extends JpaRepository<Information, Long>, JpaSpecificationExecutor<Information> {

	Information findByUuid(String uuid);

	Long countByVerifiedAndUsedAndRemoved(boolean verified, boolean used, boolean removed);

	@Modifying
	@Query("update Information i set i.used=true where i.uuid in ?1")
	int updateUsedByUuidIn(String[] uuids);

	Long countByVerifiedAndUsedAndRemovedAndIdIn(boolean verified, boolean used, boolean removed, List<Long> inforIds);
	
}
