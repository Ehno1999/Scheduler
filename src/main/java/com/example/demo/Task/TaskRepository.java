package com.example.demo.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Query(value = "SELECT * FROM task t WHERE PARSEDATETIME(t.date, 'EEE MMM dd HH:mm:ss z yyyy') >= PARSEDATETIME(:currentDate, 'yyyy-MM-dd HH:mm:ss') ORDER BY PARSEDATETIME(t.date, 'EEE MMM dd HH:mm:ss z yyyy') ASC", nativeQuery = true)
    List<Task> findClosestFutureTask(@Param("currentDate") String currentDate);
}


