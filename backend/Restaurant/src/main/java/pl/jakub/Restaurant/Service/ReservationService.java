package pl.jakub.Restaurant.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jakub.Restaurant.Data.Reservation;
import pl.jakub.Restaurant.Data.ReservationRepository;
import pl.jakub.Restaurant.Data.Table;
import pl.jakub.Restaurant.Data.Tables;
import pl.jakub.Restaurant.Exception.CannotCancelReservationException;
import pl.jakub.Restaurant.Exception.CannotDeleteReservationException;
import pl.jakub.Restaurant.Exception.CannotMakeReservationException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private MailService mailService;

    @Autowired
    private Tables tables;

    public HashMap<String, String> makeReservation(Reservation reservation) throws CannotMakeReservationException, AddressException {
        if(reservation.getDate().minusHours(2L).isBefore(LocalDateTime.now())){
            throw new CannotMakeReservationException("Too late for this reservation");
        }

        new InternetAddress(reservation.getEmail()).validate();

        Table table = tables.findByNumber(reservation.getSeatNumber()).orElseThrow(() ->
                new CannotMakeReservationException("No such table found"));

        if(table.getMinNumberOfSeats() > reservation.getNumberOfSeats())
            throw new CannotMakeReservationException("Too few seats");

        if(table.getMaxNumberOfSeats() < reservation.getNumberOfSeats())
            throw new CannotMakeReservationException("Too many seats");

        System.out.println(tables.getReservedTables(reservation.getDate(), reservation.getDuration()));

        if(tables.getReservedTables(reservation.getDate(), reservation.getDuration()).contains(reservation.getSeatNumber()))
            throw new CannotMakeReservationException("Seat already taken");

        repository.save(reservation);

        mailService.sendMail(reservation.getEmail(),
                "Reservation created",
                String.format("Id of your reservation: %s", reservation.getId()));

        HashMap<String, String> body = new HashMap<>();
        body.put("reservationId", reservation.getId());
        return body;
    }

    public HashMap<String, List<Reservation>> getReservations(LocalDateTime date){
        List<Reservation> bookings =  repository.findAll().stream()
                .filter(r -> r.getDate().truncatedTo(ChronoUnit.DAYS).isEqual(date.truncatedTo(ChronoUnit.DAYS)))
                .collect(Collectors.toList());
        return (HashMap<String, List<Reservation>>) Map.of("bookings", bookings);
    }

    public Reservation getById(String id) throws CannotMakeReservationException {
        return repository.findById(id).orElseThrow(() -> new CannotMakeReservationException());
    }

    public void cancelReservation(String id, Map<String, String> status) throws CannotCancelReservationException {
        Reservation reservation = repository.findById(id).orElseThrow();

        if(!status.containsKey("status"))
            throw new CannotCancelReservationException("Bad input.");
        if(!status.get("status").equals(Reservation.CANCELLATION))
            throw new CannotCancelReservationException("Bad input.");

        System.out.println(reservation.getDate());
        if(reservation.getDate().minusHours(2L).isBefore(LocalDateTime.now()))
            throw new CannotCancelReservationException("To late to cancel this reservation.");

        int token = generateToken();
        reservation.setToken(token);
        mailService.sendMail(reservation.getEmail(), "Cancel your reservation",
                String.format("Token for deleting your reservation: %d", token));

        repository.save(reservation);
    }

    public void deleteReservation(String id, Map<String, String> body) throws CannotDeleteReservationException {
        Reservation reservation = repository.findById(id).orElseThrow();
        if(!body.containsKey("verificationCode"))
            throw new CannotDeleteReservationException("Bad input");
        int token = Integer.parseInt(body.get("verificationCode"));

        if(token == reservation.getToken()){
            repository.deleteById(id);
        }
        else{
            throw new CannotDeleteReservationException("Bad token");
        }

    }

    private int generateToken(){
        Random r = new Random();
        return r.nextInt(9000) + 1000;
    }
}
