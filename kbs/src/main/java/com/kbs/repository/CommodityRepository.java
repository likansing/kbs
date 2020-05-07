package com.kbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Commodity;

@Repository
@Transactional
public interface CommodityRepository extends CrudRepository<Commodity, Long> {
	
	@Query("select c from Commodity c where c.commodity =?1")
	Commodity searchCommodityByName(String commodity);

	@Query("select c from Commodity c where c.id != 1")
	List<Commodity> searchAllUnlessId1();
	
}
