package com.kbs.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Platform;

@Repository
@Transactional
public interface PlatformRepository extends CrudRepository<Platform, Long> {
	
	@Query("select p from Platform p where p.platformName =?1")
	Platform searchPlatformByName(String platformName);

}
