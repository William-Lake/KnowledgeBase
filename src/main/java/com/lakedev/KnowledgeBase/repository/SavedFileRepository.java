package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakedev.KnowledgeBase.model.SavedFile;
import com.lakedev.KnowledgeBase.model.SavedNote;

@Repository
public interface SavedFileRepository extends JpaRepository<SavedFile, Integer> 
{
	Page<SavedFile> findByFileNameLikeIgnoreCase(String titleFilter, Pageable pageable);
	
	long countByFileNameLikeIgnoreCase(String titleFilter);
	
	SavedFile findByFileNameIgnoreCase(String fileName);
}