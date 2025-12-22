package com.example.medistore.repository.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.medistore.entity.batch.Law;

@Repository
public interface LawRepository extends JpaRepository<Law, String> {
}
