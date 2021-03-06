package com.kbs.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Commodity;
import com.kbs.model.Knowledge;
import com.kbs.model.Platform;
import com.kbs.model.RegionCountry;

@Repository
@Transactional
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
	
	@Query("select k from Knowledge k order by k.id DESC")
	List<Knowledge> sortAllByIdOrderByDESC();
	
	@Query("select k from Knowledge k where k.title like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByTitle(String searchWord);
	
	@Query("select k from Knowledge k where k.id = ?1")
	List<Knowledge> searchKnowledgeById(Long searchId);
	
	@Query("select k from Knowledge k where k.description like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByDescription(String searchWord);
	
	@Query("select k from Knowledge k where k.platform.platformName like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByPlatformName(String searchWord);
	
	@Query("select k from Knowledge k where k.platform.marketName like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByMarketName(String searchWord);
	
	@Query("select k from Knowledge k where k.severity like %?1% order by k.id")
	List<Knowledge> searchKnowledgeBySeverity(String searchWord);
	
	@Query("select k from Knowledge k where k.status like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByStatus(String searchWord);
	
	@Query("select k from Knowledge k where k.platform.marketName like %?1% order by k.id")
	List<Knowledge> searchKnowledgeByMarketNameLike(String searchWord);
	
	@Query("select k from Knowledge k where k.regionCountry.id = ?1 order by k.id")
	List<Knowledge> searchKnowledgeByRegionCopuntryById(Long regionCountryId);
	
	@Query("select k from Knowledge k where k.commodity.id = ?1 order by k.id")
	List<Knowledge> searchKnowledgeCommodityById(Long commodityId);
	
	@Query("select k from Knowledge k where k.commodity.id = ?1 and  k.regionCountry.id = ?2")
	List<Knowledge> searchKnowledgeByCommodityAndRegionCountry(Long commodityId, Long regionCountryId);
	
//	default Page<Knowledge> findKnowledgeByPage(Knowledge k, Commodity c, Iterable<Platform> p, RegionCountry rc, Pageable pageable){
	default Page<Knowledge> findKnowledgeByPage(Knowledge k, Commodity c, Platform p, RegionCountry rc, Pageable pageable){
		
		Knowledge knowledge = new Knowledge();
		knowledge.setCommodity(c);
		knowledge.setRegionCountry(rc);
		knowledge.setPlatform(p);
		
//		Platform plat = p.iterator().next();
//		knowledge.setPlatform(plat);
		
		
		knowledge.setDescription(k.getDescription());
		knowledge.setTitle(k.getTitle());
		knowledge.setSeverity(k.getSeverity());
		knowledge.setStatus(k.getStatus());
		
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
				.withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("platform.platformName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("platform.marketName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("platform.sysid", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("commodity", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("regionCountry", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("severity", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("status", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Knowledge> example = Example.of(knowledge, exampleMatcher);
		
		Page<Knowledge> knowledgepage = findAll(example, pageable);
		
		return knowledgepage;
		
	}
	
}
