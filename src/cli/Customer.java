package cli;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final String eventCategory;
    private final int customerRetrievalRate;
    private final int customerId;

    public Customer(TicketPool ticketPool, String eventCategory, int customerRetrievalRate, int customerId) {
        this.ticketPool = ticketPool;
        this.eventCategory = eventCategory;
        this.customerRetrievalRate = customerRetrievalRate;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ticketPool.removeTicket(eventCategory, customerId);
                Thread.sleep(1000 / customerRetrievalRate);
            }
        } catch (InterruptedException e) {
            System.out.println("Customer " + customerId + " interrupted.");
        }
    }
}


