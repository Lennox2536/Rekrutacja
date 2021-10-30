package pl.jakub.Restaurant.Data;

public class Table {

    private int number;
    private int minNumberOfSeats;
    private int maxNumberOfSeats;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMinNumberOfSeats() {
        return minNumberOfSeats;
    }

    public void setMinNumberOfSeats(int minNumberOfSeats) {
        this.minNumberOfSeats = minNumberOfSeats;
    }

    public int getMaxNumberOfSeats() {
        return maxNumberOfSeats;
    }

    public void setMaxNumberOfSeats(int maxNumberOfSeats) {
        this.maxNumberOfSeats = maxNumberOfSeats;
    }
}
