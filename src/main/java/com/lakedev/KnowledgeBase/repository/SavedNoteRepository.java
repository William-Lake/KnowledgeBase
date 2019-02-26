package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lakedev.KnowledgeBase.model.SavedNote;

@Repository
public interface SavedNoteRepository extends JpaRepository<SavedNote, Integer> 
{
	Page<SavedNote> findByNoteTitleLikeIgnoreCase(String titleFilter, Pageable pageable);
	
	long countByNoteTitleLikeIgnoreCase(String titleFilter);
}