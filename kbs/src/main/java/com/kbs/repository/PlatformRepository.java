package com.kbs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Platform;

@Repository
@Transactional
public interface PlatformRepository extends JpaRepository<Platform, Long> {
	
	@Query("select p from Platform p where p.platformName like %?1%")
	List<Platform> searchPlatformByName(String platformName);

	@Query("select p from Platform p where p.marketName like %?1%")
	List<Platform> searchPlatformByMarketName(String marketName);
	
	@Query("select p from Platform p where p.sysid like %?1%")
	List<Platform> searchPlatformBySysid(String sysid);
	
	@Query("select p from Platform p where p.platformName = ?1")
	Platform findPlatformByPlatformNameEqual(String platformName);

	@Query("select p from Platform p where p.marketName = ?1")
	Platform findPlatformByMarketNameEqual(String marketName);
	
	@Query("select p from Platform p where p.sysid = ?1")
	Platform findPlatformBySysidEqual(String sysid);
	
	default Page<Platform> findPlatformByPage(Pageable pageable){
		
		//Platform platform = new Platform();
		
//		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
//				.withMatcher("platformName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
//				.withMatcher("marketName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
//				.withMatcher("sysid", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
//		
//		Example<Knowledge> example = Example.of(knowledge, exampleMatcher);
		Page<Platform> Platformpage = findAll(pageable);
		return Platformpage;
	}
}
