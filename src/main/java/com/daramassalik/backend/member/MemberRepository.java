package com.daramassalik.backend.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByActiveTrueOrderByLastNameAscFirstNameAsc();

    @Query("""
            select m from Member m
            where m.active = true
              and (lower(m.firstName) like lower(concat('%', :search, '%'))
                   or lower(m.lastName) like lower(concat('%', :search, '%')))
            order by m.lastName, m.firstName
            """)
    List<Member> searchActive(@Param("search") String search);
}
