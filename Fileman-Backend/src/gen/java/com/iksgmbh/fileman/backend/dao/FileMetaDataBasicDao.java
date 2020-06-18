package com.iksgmbh.fileman.backend.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.iksgmbh.fileman.backend.FileMetaData;

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
public class FileMetaDataBasicDao
{
	@PersistenceContext
	protected EntityManager entityManager;

	public List<FileMetaData> findAllFileMetaDatas() {
		CriteriaQuery<FileMetaData> criteria = entityManager.getCriteriaBuilder().createQuery(FileMetaData.class);
		criteria.select(criteria.from(FileMetaData.class));
		return entityManager.createQuery(criteria).getResultList();
	}

	public FileMetaData findByName(String name) {
		return entityManager.find(FileMetaData.class, name);
	}

	public boolean update(FileMetaData entity) {
		try {
			entityManager.persist(entity);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public FileMetaData create(FileMetaData entity) {
		entityManager.persist(entity);
		return entity;
	}

	public void delete(FileMetaData entity) {
		entityManager.remove(entity);
	}
}