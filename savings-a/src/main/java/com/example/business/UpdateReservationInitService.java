package com.example.business;

import com.example.db.relational.entity.BalanceUpdateConfirmEntity;
import com.example.db.relational.entity.BalanceUpdateReservationEntity;
import com.example.db.relational.entity.BalanceUpdateUndoEntity;
import com.example.db.relational.repository.BalanceUpdateConfirmRepository;
import com.example.db.relational.repository.BalanceUpdateReservationRepository;
import com.example.db.relational.repository.BalanceUpdateUndoRepository;
import com.example.util.SelfReferential2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UpdateReservationInitService implements SelfReferential2<UpdateReservationInitService> {

    @Getter @Setter
    UpdateReservationInitService self;

    final BalanceUpdateReservationRepository updateReservationRepository;
    final BalanceUpdateConfirmRepository confirmRepository;
    final BalanceUpdateUndoRepository undoRepository;

    /**
     * Creates and commits an initial record of the update reservation. Block calls incoming from methods that have open
     * transactions to not give the idea that the insert will be reverted if the transaction gets rolled back.
     *
     */
    @Transactional(
            propagation = Propagation.NEVER
    )
    public UUID initUpdateReservation(String idemCode, String idemActor, ZonedDateTime requestTimestamp,
            BigDecimal amount) {

        final var reservationCode = UUID.randomUUID();

        BalanceUpdateReservationEntity balanceUpdateReservationEntity = BalanceUpdateReservationEntity.builder()
                .amount(amount)
                .reservationCode(reservationCode)
                .idempotencyCode(idemCode)
                .idempotencyActor(idemActor)
                .requestTimestamp(requestTimestamp)
                .status(BalanceUpdateReservationEntity.BalanceUpdateReservationStatus.RECEIVED.getDbValue())
                .build();

        log.info("idemCode={} idemActor={} reservationCode={} a=SavingBalanceUpdateReservationEntity", idemCode, idemActor, reservationCode);
        updateReservationRepository.save(balanceUpdateReservationEntity);

        return reservationCode;
    }

    /**
     * Creates and commits an initial record of the entity. Block calls incoming from methods that have open
     * transactions to not give the idea that the insert will be reverted if the transaction gets rolled back.
     *
     * The purpose is to record the reception of an update reservation confirmation, and for it to exist on database even if some
     * error happens later in the flow.
     */
    @Transactional(
            propagation = Propagation.NEVER
    )
    @Nonnull
    public UUID initConfirmation(BalanceUpdateReservationEntity updateReservation, ZonedDateTime requestTimestamp) {
        final BalanceUpdateConfirmEntity confirmation = BalanceUpdateConfirmEntity.builder()
                .balanceUpdateReservationEntity(updateReservation)
                .requestTimestamp(requestTimestamp)
                .done(false)
                .build();
        log.info("reservationCode={} a=SavingBalanceUpdateConfirmEntity", updateReservation.getReservationCode());
        confirmRepository.save(confirmation);
        return confirmation.getId();
    }

    @Transactional(
            propagation = Propagation.NEVER
    )
    @Nonnull
    public UUID initConfirmation(UUID updateReservationCode, ZonedDateTime requestTimestamp)
            throws ConfirmUnknownReservationException {

        final var reservation = updateReservationRepository.findByReservationCode(updateReservationCode);
        if (reservation.isPresent()) {
            return self.initConfirmation(reservation.get(), requestTimestamp);
        }

        throw new ConfirmUnknownReservationException(updateReservationCode);
    }

    /**
     * Creates and commits an initial record of the entity. Block calls incoming from methods that have open
     * transactions to not give the idea that the insert will be reverted if the transaction gets rolled back.
     *
     * The purpose is to record the reception of an update reservation undo, and for it to exist on database even if some
     * error happens later in the flow.
     */
    @Transactional(
            propagation = Propagation.NEVER
    )
    @Nonnull
    public UUID initUndo(BalanceUpdateReservationEntity updateReservation, ZonedDateTime requestTimestamp) {
        final var undo = BalanceUpdateUndoEntity.builder()
                .balanceUpdateReservationEntity(updateReservation)
                .requestTimestamp(requestTimestamp)
                .done(false)
                .build();
        log.info("reservationCode={} a=SavingBalanceUpdateUndoEntity", updateReservation.getReservationCode());
        undoRepository.save(undo);
        return undo.getId();
    }

    @Transactional(
            propagation = Propagation.NEVER
    )
    @Nonnull
    public UUID initUndo(UUID updateReservationCode, ZonedDateTime requestTimestamp)
            throws UndoUnknownReservationException {

        final var reservation = updateReservationRepository.findByReservationCode(updateReservationCode);
        if (reservation.isPresent()) {
            return self.initUndo(reservation.get(), requestTimestamp);
        }

        throw new UndoUnknownReservationException(updateReservationCode);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class ConfirmUnknownReservationException extends Exception {
        UUID reservationCode;

        public ConfirmUnknownReservationException(UUID reservationCode) {
            this.reservationCode = reservationCode;
        }
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class UndoUnknownReservationException extends Exception {
        UUID reservationCode;

        public UndoUnknownReservationException(UUID reservationCode) {
            this.reservationCode = reservationCode;
        }
    }
}
