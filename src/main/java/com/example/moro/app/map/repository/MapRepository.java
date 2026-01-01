package com.example.moro.app.map.repository;

import com.example.moro.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<Post,Long> {
    //주소 또는 내용 기반 검색
    
    //좌표 기반 반경 검색
}
