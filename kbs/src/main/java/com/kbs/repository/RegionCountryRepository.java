package com.kbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.RegionCountry;

@Repository
@Transactional
public interface RegionCountryRepository extends JpaRepository<RegionCountry, Long> {

	@Query("select r from RegionCountry r where r.country =?1")
	RegionCountry searchRegionCountryByCountryName(String country);
	
	@Query("select r from RegionCountry r")
	RegionCountry searchAllData();
	
	@Query("select r from RegionCountry r where r.country != ?1")
	List<RegionCountry> testeNovo(String country);

}
