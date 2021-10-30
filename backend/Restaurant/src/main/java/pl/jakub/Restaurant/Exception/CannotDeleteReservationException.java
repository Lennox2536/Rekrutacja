package pl.jakub.Restaurant.Exception;

public class CannotDeleteReservationException extends Exception{
    public CannotDeleteReservationException(){
        super();
    }

    public CannotDeleteReservationException(String message){
        super(message);
    }
}
