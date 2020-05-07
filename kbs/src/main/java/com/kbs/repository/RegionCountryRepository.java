package com.kbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.RegionCountry;

@Repository
@Transactional
public interface RegionCountryRepository extends CrudRepository<RegionCountry, Long> {

	@Query("select r from RegionCountry r where r.country =?1")
	RegionCountry searchRegionCountryByCountryName(String country);
	
	@Query("select r from RegionCountry r")
	RegionCountry searchAllData();
	
	@Query("select r from RegionCountry r where r.id != 1")
	List<RegionCountry> searchAllUnlessId1();

}
