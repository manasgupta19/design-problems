package MachineCoding.SeatBooking.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Seat {
    private final String seatId;
    private final double price;
    private volatile SeatStatus seatStatus;
    private final ReentrantLock lock;

    public Seat(String seatId, double price) {
        this.seatId = seatId;
        this.price = price;
        this.seatStatus = SeatStatus.AVAILABLE;
        this.lock = new ReentrantLock(true);
    }

    public String getSeatId() {
        return seatId;
    }

    public double getPrice() {
        return price;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public boolean lockSeat(long timeout, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(timeout, unit);
    }

    public void unlockSeat() {
        lock.unlock();
    }
}
