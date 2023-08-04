package com.mauricio.inventory.views;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CompleteTransactionRepository extends JpaRepository<CompleteTransaction, String> {

}
