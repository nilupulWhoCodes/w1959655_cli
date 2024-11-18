package Class;

public class Configuration {
    private int totalNumberOfTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    public Configuration(Integer totalNumberOfTickets, Integer ticketReleaseRate, Integer customerRetrievalRate, Integer maxTicketCapacity) {
        this.totalNumberOfTickets = totalNumberOfTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public int getTotalNumberOfTickets() {
        return totalNumberOfTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    @Override
    public String toString() {
        return "Configuration : {" +
                "Total Number of Tickets: " + totalNumberOfTickets +
                ", Ticket Release Rate: " + ticketReleaseRate +
                ", Customer Retrieval Rate: " + customerRetrievalRate +
                ", Max Ticket Capacity: " + maxTicketCapacity +
                '}';
    }
}
