package com.example.project1.Repository;

import com.example.project1.Connection;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
}
