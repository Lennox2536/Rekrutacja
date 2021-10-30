package pl.jakub.Restaurant.Exception;

public class CannotCancelReservationException extends Exception{

    public CannotCancelReservationException(){
        super();
    }
    public CannotCancelReservationException(String message){
        super(message);
    }
}
