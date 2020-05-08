package com.kbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Commodity;

@Repository
@Transactional
public interface CommodityRepository extends JpaRepository<Commodity, Long> {
	
	@Query("select c from Commodity c where c.commodity =?1")
	Commodity searchCommodityByName(String commodity);

	@Query("select c from Commodity c where c.commodity != ?1")
	List<Commodity> teste(String commodity);
	
}
