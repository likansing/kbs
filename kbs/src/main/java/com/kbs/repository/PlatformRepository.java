package com.kbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Platform;

@Repository
@Transactional
public interface PlatformRepository extends CrudRepository<Platform, Long> {
	
	@Query("select p from Platform p where p.platformName like %?1%")
	List<Platform> searchPlatformByName(String platformName);

	@Query("select p from Platform p where p.marketName like %?1%")
	List<Platform> searchPlatformByMarketName(String marketName);
	
	@Query("select p from Platform p where p.sysid like %?1%")
	List<Platform> searchPlatformBySysid(String sysid);
	
}
