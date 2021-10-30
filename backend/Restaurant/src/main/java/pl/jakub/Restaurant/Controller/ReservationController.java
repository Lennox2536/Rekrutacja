package pl.jakub.Restaurant.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.jakub.Restaurant.Data.Reservation;
import pl.jakub.Restaurant.Data.ReservationRepository;
import pl.jakub.Restaurant.Exception.CannotCancelReservationException;
import pl.jakub.Restaurant.Exception.CannotDeleteReservationException;
import pl.jakub.Restaurant.Exception.CannotMakeReservationException;
import pl.jakub.Restaurant.Service.ReservationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository repository;


    @PostMapping
    public ResponseEntity<HashMap<String, String>> makeReservation(@RequestBody Reservation reservation){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.makeReservation(reservation));
        }
        catch (CannotMakeReservationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<HashMap<String, List<Reservation>>> getReservations(@RequestParam LocalDateTime date){
        return ResponseEntity.ok(reservationService.getReservations(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getOne(@PathVariable String id){
        try{
            return ResponseEntity.ok(reservationService.getById(id));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public void cancelReservation(@PathVariable String id, @RequestBody Map<String, String> status){
        try{
            reservationService.cancelReservation(id, status);
        } catch (CannotCancelReservationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable String id, @RequestBody Map<String, String> body){
        try{
            reservationService.deleteReservation(id, body);
        }
        catch (CannotDeleteReservationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad input");
        }
        catch (NumberFormatException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad token format");
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
