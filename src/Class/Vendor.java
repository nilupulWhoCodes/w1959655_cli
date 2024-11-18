package Class;

import Logger.Logger;

public class Vendor implements Runnable {
    private int vendorId;
    private int ticketsPerRelease;
    private int releaseInterval;
    private TicketPool ticketPool;

    public Vendor(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (true) {
            try{
                ticketPool.addTicket();
                Thread.sleep(2000);
                if (ticketPool.checkAvailability()){
                    Logger.info("No more tickets left to release.");
                    break;
                }
            } catch (InterruptedException e) {
                Logger.error("An error occurred while running the vendor thread");
            }
        }
    }
}
