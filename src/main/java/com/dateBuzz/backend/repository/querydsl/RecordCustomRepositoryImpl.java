package com.dateBuzz.backend.repository.querydsl;

import com.dateBuzz.backend.repository.RecordCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecordCustomRepositoryImpl implements RecordCustomRepository {

    private final JPAQueryFactory queryFactory;

}
