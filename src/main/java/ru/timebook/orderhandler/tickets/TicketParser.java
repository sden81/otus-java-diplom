package ru.timebook.orderhandler.tickets;

import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

public interface TicketParser {
    Order parseTicket(Ticket ticket);
}
