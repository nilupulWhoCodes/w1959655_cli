package Class;

import Class.Configuration;
import Logger.Logger;

import java.util.LinkedList;


public class TicketPool {

    private final Configuration configuration;
    private final int maxPoolSize;
    private int releasedTicketCount;
    private final LinkedList<Integer> ticketList = new LinkedList<>();

    public TicketPool(Configuration configuration) {
        this.configuration = configuration;
        this.maxPoolSize = configuration.getMaxTicketCapacity();
    }

    public synchronized void addTicket( ) {
        try{
            int ticketReleaseRate = configuration.getTicketReleaseRate();
            while (ticketList.size() == maxPoolSize) {
                Logger.warning("Ticket pool is full. Wait till customer purchase tickets.");
                wait();
            }
            ticketReleaseRate = Math.min(maxPoolSize - ticketList.size(), ticketReleaseRate);

            for (int i = 0; i < ticketReleaseRate; i++) {
                if(releasedTicketCount >= configuration.getTotalNumberOfTickets()) {
                    notifyAll();
                    return;
                }
                ticketList.add(releasedTicketCount++);
            }
            Logger.info(ticketReleaseRate + " tickets added. Total: " + ticketList.size());
            notifyAll();

        }catch(InterruptedException e){
            Logger.error("An Error occurred while adding ticket to the pool. " + e.getMessage());
        }
    }

    public synchronized void removeTicket( ) {
        try{
            int customerRetrievalRate = configuration.getCustomerRetrievalRate();
            while (ticketList.isEmpty()) {
                if (releasedTicketCount >= configuration.getTotalNumberOfTickets()) {
                    Logger.warning("No more tickets for this event.");
                    notifyAll();
                } else {
                    Logger.warning("Ticket pool is empty. Wait till vendor release tickets.");
                    wait();
                }
            }
            customerRetrievalRate = Math.min(ticketList.size(), customerRetrievalRate);

            for (int i = 0; i < customerRetrievalRate; i++) {
                if(!ticketList.isEmpty()) {
                   ticketList.removeFirst();
                }
            }
            Logger.info(customerRetrievalRate + " tickets purchased. Total: " + ticketList.size());
            notifyAll();

        }catch(InterruptedException e){
            Logger.error("An Error occurred while adding ticket to the pool. " + e.getMessage());
        }
    }

    public boolean checkAvailability() {
        return releasedTicketCount > configuration.getTotalNumberOfTickets() && ticketList.isEmpty();
    }
}
