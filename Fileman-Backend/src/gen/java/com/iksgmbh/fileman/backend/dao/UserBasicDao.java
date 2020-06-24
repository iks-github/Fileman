package com.iksgmbh.fileman.backend.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.iksgmbh.fileman.backend.User;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Component
/**
 * Implementation of basic dao functionalities.
 * This file is autogenerated by MOGLiCC. Do not modify manually!
 */
@Repository
@Transactional
public class UserBasicDao
{
	@PersistenceContext
	protected EntityManager entityManager;

	public List<User> findAllUsers() {
		CriteriaQuery<User> criteria = entityManager.getCriteriaBuilder().createQuery(User.class);
		criteria.select(criteria.from(User.class));
		return entityManager.createQuery(criteria).getResultList();
	}

	public User findById(Integer id) {
		return entityManager.find(User.class, id);
	}

	public boolean update(User entity) {
		try {
			entityManager.persist(entity);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public User findByName(String name)
	{
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		criteria.select(root).where(criteriaBuilder.equal(root.get("name"), name));
		return entityManager.createQuery(criteria).getSingleResult();
	}


	public User create(User entity) {
		entityManager.persist(entity);
		return entity;
	}

	public void delete(User entity) {
		entityManager.remove(entity);
	}
}