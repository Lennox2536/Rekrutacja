package pl.jakub.Restaurant.Data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Tables {
    private static final String path = "src\\main\\resources\\tables.json";
    public  List<Table> tables;

    @Autowired
    private ReservationRepository repository;

    public Tables() throws IOException {
        tables = init();

    }

    public List<Table> init() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root =  mapper.readTree(Files.readString(Path.of(path)));
        List<Table> tables = mapper.readValue(root.withArray("tables").toString(), new TypeReference<>() {
        });
        tables.forEach(System.out::println);
        return tables;
    }

    public Optional<Table> findByNumber(int number){
        return tables.stream().filter(t -> t.getNumber() == number).findFirst();
    }

    public Map<String, List<Table>> getTables(String status, int min_seats, LocalDateTime start_date, String duration){
        if(!status.equals("free"))
            throw new UnsupportedOperationException();

        if(min_seats <= 0)
            throw new IllegalArgumentException();

        tables.stream().filter(t -> t.getMinNumberOfSeats() == min_seats).collect(Collectors.toList());

        Set<Integer> seatNumbers = getReservedTables(start_date, Integer.parseInt(duration));


        List<Table> freeTables = tables.stream().filter(t -> !seatNumbers.contains(t.getNumber())).collect(Collectors.toList());
        HashMap<String, List<Table>> output = new HashMap<>();
        output.put("tables", freeTables);
        return output;
    }

    public Set<Integer> getReservedTables(LocalDateTime start_date, int duration){
        List<Reservation> reservations = repository.findAll();


        reservations = reservations.stream()
                .filter(r -> {
                    LocalDateTime date = r.getDate().plusHours(r.getDuration());
                    return (date.isAfter(start_date) || date.isEqual(start_date));
                }  )
                .filter(r -> {
                    LocalDateTime date = r.getDate();
                    LocalDateTime date2 = start_date.plusHours(duration);
                    return (date.isBefore(date2) || date.isEqual(date2));
                }  )
                .collect(Collectors.toList());

        HashSet<Integer> seatNumbers = new HashSet<>();

        for(Reservation r : reservations){
            seatNumbers.add(r.getSeatNumber());
        }

        return seatNumbers;
    }
}
