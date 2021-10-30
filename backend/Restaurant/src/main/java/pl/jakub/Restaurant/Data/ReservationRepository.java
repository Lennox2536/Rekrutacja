package pl.jakub.Restaurant.Data;

import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findBySeatNumber(int seatNumber);
}
