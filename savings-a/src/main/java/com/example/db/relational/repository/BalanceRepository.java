package com.example.db.relational.repository;

import com.example.db.relational.entity.BalanceEntity;
import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BalanceRepository extends PagingAndSortingRepository<BalanceEntity, UUID> {

    int TIMEOUT_MS = 3000;
    String QUERY_GET_AMOUNT = "select " + BalanceEntity.COL_AMOUNT + " from " + BalanceEntity.TABLE_NAME_BALANCE
            + " limit 1";

//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("update balance b set b.amount = b.amount + :delta where b = :bal and b.amount + :delta >= 0")
//    @Transactional(propagation = Propagation.REQUIRED, timeout = TIMEOUT_MS)
//    int updateBalanceBy(@Param("bal") BalanceEntity balanceEntity, @Param("delta") BigDecimal delta);

    @Query(value = QUERY_GET_AMOUNT, nativeQuery = true)
    @Transactional(propagation = Propagation.SUPPORTS)
    List<BigDecimal> getBalanceAmount();

    @Query(value = "select b from balance b")
    @Transactional(propagation = Propagation.REQUIRED, timeout = TIMEOUT_MS)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BalanceEntity> getBalanceForUpdate(Pageable pageable);

    //    @Transactional
    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //    @Query("SELECT b FROM balance b")
    //    @QueryHints({@QueryHint(name = JPA_LOCK_TIMEOUT, value = TIMEOUT_MS)})
    //    List<BalanceEntity> lockToWriteFindAny(Pageable pageable);
}