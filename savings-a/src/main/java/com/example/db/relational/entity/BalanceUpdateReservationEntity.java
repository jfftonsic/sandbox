package com.example.db.relational.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity(name = BalanceUpdateReservationEntity.ENTITY_NAME_BALANCE_UPDATE_RESERVATION)
@Table(name = BalanceUpdateReservationEntity.TABLE_NAME_BALANCE_UPDATE_RESERVATION)
@Getter @Setter @ToString @RequiredArgsConstructor @SuperBuilder
public class BalanceUpdateReservationEntity {

    public static final String TABLE_NAME_BALANCE_UPDATE_RESERVATION = "balance_update_reservation";
    public static final String ENTITY_NAME_BALANCE_UPDATE_RESERVATION = "balanceUpdateReservation";
    private static final String COL_ID = "id";
    private static final String COL_IDEM_CODE = "idem_code";
    private static final String COL_IDEM_ACTOR = "idem_actor";
    private static final String COL_RESERVATION_CODE = "reservation_code";
    private static final String COL_STATUS = "status";
    private static final String COL_REQUEST_TIMESTAMP = "request_timestamp";
    private static final String COL_AMOUNT = "amount";

    @Getter
    public enum BalanceUpdateReservationStatus {
        UNMAPPED(-1), RECEIVED(0), RESERVED(1), CONFIRMED(2), CANCELED(3), BUSINESS_RULE_VIOLATION(4);

        private int dbValue;

        BalanceUpdateReservationStatus(int dbValue) {
            this.dbValue = dbValue;
        }

        public static BalanceUpdateReservationStatus fromDbValue(int fromDbValue) {
            return Arrays.stream(values()).filter(x -> x.getDbValue() == fromDbValue).findAny().orElse(UNMAPPED);
        }
    }

    @Id
    @Column(name = COL_ID, nullable = false)
    final private UUID id = UUID.randomUUID();

    @Column(name = COL_IDEM_CODE, nullable = false)
    private String idempotencyCode;

    @Column(name = COL_IDEM_ACTOR, nullable = false)
    private String idempotencyActor;

    @Column(name = COL_RESERVATION_CODE, nullable = false)
    private UUID reservationCode;

    @Column(name = COL_STATUS, nullable = false)
    private Integer status;

    @Column(name = COL_REQUEST_TIMESTAMP, nullable = false)
    private ZonedDateTime requestTimestamp;

    @Column(name = COL_AMOUNT, nullable = false)
    private BigDecimal amount;

    @Transient
    public BalanceUpdateReservationStatus getStatusEnum() {
        return BalanceUpdateReservationStatus.fromDbValue(getStatus());
    }

    @Transient
    public void setStatusEnum(BalanceUpdateReservationStatus st) {
        setStatus(st.getDbValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        BalanceUpdateReservationEntity that = (BalanceUpdateReservationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
