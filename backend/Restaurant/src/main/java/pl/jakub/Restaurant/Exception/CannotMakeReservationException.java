package pl.jakub.Restaurant.Exception;

public class CannotMakeReservationException extends Exception{
    public CannotMakeReservationException(){
        super();
    }
    public CannotMakeReservationException(String message){
        super(message);
    }
}
