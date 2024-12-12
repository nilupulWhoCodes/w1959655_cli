package cli;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final String eventCategory;
    private final int ticketReleaseRate;
    private final int vendorId;

    public Vendor(TicketPool ticketPool, String eventCategory, int ticketReleaseRate, int vendorId) {
        this.ticketPool = ticketPool;
        this.eventCategory = eventCategory;
        this.ticketReleaseRate = ticketReleaseRate;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (Vendor.class) {
                    ticketPool.addTicket(eventCategory, vendorId);
                }
                Thread.sleep(1000 / ticketReleaseRate);
            }
        } catch (InterruptedException e) {
            System.out.println("Vendor " + vendorId + " interrupted.");
        }
    }
}
