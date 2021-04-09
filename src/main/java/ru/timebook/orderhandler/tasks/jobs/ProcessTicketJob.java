package ru.timebook.orderhandler.tasks.jobs;

import ru.timebook.orderhandler.tickets.TicketService;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.concurrent.BlockingQueue;

public class ProcessTicketJob implements Runnable{
    private final BlockingQueue<Ticket> needProcessTicketsQueue;
    private final TicketService ticketService;

    public ProcessTicketJob(BlockingQueue<Ticket> needProcessTicketsQueue, TicketService ticketService) {
        this.needProcessTicketsQueue = needProcessTicketsQueue;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        try {
            while (true){
                ticketService.processTicket(needProcessTicketsQueue.take());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
