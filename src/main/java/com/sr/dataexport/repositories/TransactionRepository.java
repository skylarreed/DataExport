package com.sr.dataexport.repositories;

import com.sr.dataexport.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{}

