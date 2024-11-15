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
