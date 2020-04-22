package com.kbs.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kbs.model.Knowledge;

@Repository
@Transactional
public class DinamicHql implements Serializable{

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public List<Knowledge> executaHqlDinamic(String hql){
		return entityManager.createQuery(hql).getResultList();
	}
	
}
