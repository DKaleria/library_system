package com.example.bookorderservice.repository;

import com.example.bookorderservice.repository.entity.BookOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOrderRepository extends JpaRepository<BookOrderEntity, Long> {
}
