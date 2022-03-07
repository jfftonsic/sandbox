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
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity(name = BalanceEntity.ENTITY_NAME_BALANCE)
@Table(name = BalanceEntity.TABLE_NAME_BALANCE)
@Getter @Setter @ToString @RequiredArgsConstructor @SuperBuilder
public class BalanceEntity {

    public static final String TABLE_NAME_BALANCE = "balance";
    public static final String ENTITY_NAME_BALANCE = "balance";
    public static final String COL_ID = "id";
    public static final String COL_AMOUNT = "amount";

    /** A primary key is required for the table. The easiest way overall for the current requirements
     * would be to use a fixed number. I'm using UUID because it is more future-proof. We've had some
     * problems with sequential ID's in the past.*/
    @Id
    @Column(name = COL_ID, nullable = false)
    final private UUID id = UUID.randomUUID();

    @Column(name = COL_AMOUNT, nullable = false)
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        BalanceEntity that = (BalanceEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
