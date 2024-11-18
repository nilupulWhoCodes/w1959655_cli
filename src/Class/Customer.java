package Class;

import Logger.Logger;

public class Customer implements Runnable {
    private int customerId;
    private int retrievalInterval;
    private TicketPool ticketPool;

    public Customer(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ticketPool.removeTicket();
                if(ticketPool.checkAvailability()){
                    Logger.info("No more tickets available to purchase.");
                    break;
                }
                Thread.sleep(100);
            }catch (InterruptedException e){
                Logger.error("Error occurred while running the customer thread. " +e.getMessage());
            }
        }
    }
}
