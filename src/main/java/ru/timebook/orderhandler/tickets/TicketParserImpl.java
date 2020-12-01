package ru.timebook.orderhandler.tickets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.timebook.orderhandler.tickets.exceptions.ParsingException;
import ru.timebook.orderhandler.tickets.domain.Company;
import ru.timebook.orderhandler.tickets.domain.Month;
import ru.timebook.orderhandler.tickets.domain.Order;
import ru.timebook.orderhandler.tickets.domain.Ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TicketParserImpl implements TicketParser {
    Logger logger = LoggerFactory.getLogger(TicketParserImpl.class);

    @Override
    public Order parseTicket(Ticket ticket) {
        String htmlContent = ticket.getDescription();

        if (htmlContent == null || htmlContent.isEmpty()) {
            throw new ParsingException("Parsing content not found");
        }

        Document doc = Jsoup.parse(htmlContent);

        //тут могут в дальнейшем возникнуть проблемы, если изменится документ для парсинга
        Map<String, Element> parsingItemsMap = new HashMap<>();
        parsingItemsMap.put("orderId", doc.select("#orderId").first());
        parsingItemsMap.put("consumerName", doc.select("#consumerName").first());
        parsingItemsMap.put("totalSum", doc.select("#totalSum").first());
        parsingItemsMap.put("itemCount", doc.select("#itemCount").first());
        parsingItemsMap.put("reportingDate", doc.select("#reportingDate").first());
        parsingItemsMap.put("companyName", doc.select("#companyName").first());

        Map<String, String> parsedItems = new HashMap<>();
        parsingItemsMap.forEach((name, element) -> {
            if (element == null) {
                throw new ParsingException(String.format("%s parsing error", name));
            }
            String elementText = element.text();
            if (elementText.isEmpty()){
                logger.error(String.format("Parsed element %s is empty. It's no good!!", name));
            }
            parsedItems.put(name, element.text());
        });

        return Order.builder()
                .orderId(parsedItems.get("orderId"))
                .consumerName(parsedItems.get("consumerName"))
                .totalSum(Integer.parseInt(parsedItems.get("totalSum").replace(",00 RUB", "").replace("\u00a0", "")))
                .itemCount(Integer.parseInt(parsedItems.get("itemCount").replace("(EA)", "")))
                .company(Company.parseCompany(parsedItems.get("companyName")))
                .reportingMonth(Month.parseMonth(parsedItems.get("reportingDate"), 3))
                .year(parseYear(parsedItems.get("reportingDate")))
                .build();
    }

    private int parseYear(String rawString) {
        Pattern pattern = Pattern.compile("20[0-9]{2}");
        Matcher matcher = pattern.matcher(rawString);
        if (!matcher.find()) {
            throw new ParsingException("Can't parser year from " + rawString);
        }

        return Integer.parseInt(matcher.group(0));
    }
}
