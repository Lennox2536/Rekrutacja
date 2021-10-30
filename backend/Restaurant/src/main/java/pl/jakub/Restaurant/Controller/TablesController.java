package pl.jakub.Restaurant.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.jakub.Restaurant.Data.Table;
import pl.jakub.Restaurant.Data.Tables;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tables")
public class TablesController {

    @Autowired
    private Tables tables;

    @GetMapping
    public ResponseEntity<Map<String, List<Table>>> getTables(@RequestParam String status, @RequestParam int min_seats,
                                                              @RequestParam LocalDateTime start_date, @RequestParam String duration){
        try{
            return ResponseEntity.ok(tables.getTables(status, min_seats, start_date, duration));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
