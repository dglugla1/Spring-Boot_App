package com.example.project1.Repository;

import com.example.project1.App;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AppRepository extends JpaRepository<App, Integer> {
    App findTopByOrderByIdDesc();
}
