package cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maxCapacity;
    private final HashMap<String, Queue<Integer>> eventTickets;
    private static int globalTicketCounter = 1;
    private int totalTicketsAdded = 0;
    private final int totalTicketsLimit;

    private boolean isGlobalLimitReached = false;
    private boolean isGlobalLimitMessageLogged = false;

    public synchronized boolean isGlobalLimitReached() {
        return isGlobalLimitReached;
    }

    public TicketPool(int maxCapacity, int totalTicketsLimit) {
        this.maxCapacity = maxCapacity;
        this.eventTickets = new HashMap<>();
        this.totalTicketsLimit = totalTicketsLimit;
    }

    public synchronized void addTicket(String eventCategory, int vendorId) throws InterruptedException {
        eventTickets.putIfAbsent(eventCategory, new LinkedList<>());
        Queue<Integer> tickets = eventTickets.get(eventCategory);

        while (getTotalTicketCount() >= maxCapacity || totalTicketsAdded >= totalTicketsLimit) {
            if (totalTicketsAdded >= totalTicketsLimit) {
                isGlobalLimitReached = true;
                if (!isGlobalLimitMessageLogged) {
                    log(" -------- Vendor " + vendorId + " is waiting. Total ticket limit reached for all events.", "Vendor");
                    isGlobalLimitMessageLogged = true;
                }
                return;
            }
            if (tickets.size() >= maxCapacity) {
                log(" -------- Vendor " + vendorId + " is waiting. TicketPool is full for " + eventCategory, "Vendor");
                wait();
            }
        }

        int ticketId = ++globalTicketCounter;
        tickets.add(ticketId);
        totalTicketsAdded++;
        log(" -------- Vendor " + vendorId + " added Ticket ID #" + ticketId + " for " + eventCategory +
                " | Total Tickets Added: " + totalTicketsAdded, "Vendor");
        notifyAll();
    }

    public synchronized boolean isPoolEmptyAndLimitReached() {
        return totalTicketsAdded >= totalTicketsLimit && getTotalTicketCount() == 0;
    }

    public synchronized int removeTicket(String eventCategory, int customerId) throws InterruptedException {
        while (!eventTickets.containsKey(eventCategory) || eventTickets.get(eventCategory).isEmpty()) {
            log(" ------ Customer " + customerId + " is waiting. No tickets available for " + eventCategory, "Customer");
            wait();
        }

        Queue<Integer> tickets = eventTickets.get(eventCategory);
        int ticketId = tickets.poll();
        log(" ------ Customer " + customerId + " purchased Ticket ID #" + ticketId + " for " + eventCategory + " | Total Tickets: " + getTotalTicketCount(), "Customer");
        notifyAll();
        return ticketId;
    }

    public synchronized int getTotalTicketsAdded() {
        return totalTicketsAdded;
    }

    public synchronized String getEventSpecificCounts() {
        StringBuilder counts = new StringBuilder();
        for (String event : eventTickets.keySet()) {
            int count = eventTickets.get(event).size();
            counts.append(event).append(": ").append(count).append(", ");
        }
        if (counts.length() > 0) {
            counts.setLength(counts.length() - 2);
        }
        return counts.toString();
    }

    public synchronized int getTotalTicketCount() {
        return eventTickets.values().stream().mapToInt(Queue::size).sum();
    }

    private void log(String message, String source) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println("[" + timestamp + "] ------ [" + source + "] " + message);
    }
}
