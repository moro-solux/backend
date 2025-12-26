package com.example.moro.app.member.repository;

import com.example.moro.app.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 데이터 접근을 위한 repository
 * jpaRepository를 상속받아 기본적인 CRUD 및 쿼리 메서드를 제공함.
 */

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일 중복 여부 확인
     * @param email 확인할 이메일 주소
     * @return 존재하면 true, 없으면 false (회원가입 시 중복 검증용)
     */
    boolean existsByEmail(String email);

    /**
     * 이메일을 통한 회원 정보 조회
     * @param email 조회할 회원의 이메일
     * @return 객체를 포함한 optional(로그인, 인증 프로세스에서 사용)
     */
    Optional<Member> findByEmail(String email);

    Page<Member> findByUserNameContaining(String keyword, Pageable pageable);
}
