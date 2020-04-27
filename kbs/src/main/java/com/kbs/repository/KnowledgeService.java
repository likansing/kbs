package com.kbs.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kbs.model.Knowledge;

@Service
public class KnowledgeService {
	
	@Autowired
	private EntityManagerFactory emf;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private KnowledgeRepository knowledgeRepository;
	
	public Page<Knowledge> listAll(int pageNumber){
		Sort sort = Sort.by("id").descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
		return knowledgeRepository.findAll(pageable);
	}
	
	@SuppressWarnings("unchecked")
	public List<Knowledge> executaHqlDinamicService(String hql, int pageNumber){
		entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		
		pageNumber = pageNumber - 1;
		
		List<Knowledge> list = entityManager.createQuery(hql).setFirstResult(pageNumber * 5).setMaxResults(5).getResultList();
		entityManager.getTransaction().commit();
		entityManager.close();
		return list;
	}
	
	public int getTotalQtyOfRecord(String hql){
		entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		
		int qtyRegisters = entityManager.createQuery(hql).getResultList().size();
		
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return qtyRegisters;
	}

}
