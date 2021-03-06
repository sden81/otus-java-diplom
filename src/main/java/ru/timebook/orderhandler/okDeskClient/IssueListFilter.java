package ru.timebook.orderhandler.okDeskClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import ru.timebook.orderhandler.okDeskClient.dto.StatusCodes;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class IssueListFilter {
    @Singular("addStatus")
    private final List<StatusCodes> status;
    @Singular("addStatusNot")
    private final List<StatusCodes> status_not;
    private final LocalDate created_since;
    private final LocalDate created_until;
    private final LocalDate updated_since;
    private final LocalDate updated_until;
    @Singular("addAuthorId")
    private final List<Integer> author_contact_ids;

    public List<String> generateParams() {
        List<String> paramsList = new ArrayList<>();

        // object to Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(this, Map.class);
        map.entrySet().forEach(e -> paramsList.add(fieldToStringConverter(e.getKey(), e.getValue())));

        return paramsList.stream().filter(i -> !i.isEmpty()).collect(Collectors.toList());
    }

    private String fieldToStringConverter(String key, Object field) {
        if (field == null) {
            return "";
        }

        //для списков
        if (field instanceof List) {
            ArrayList<String> resultsList = new ArrayList<>();
            ((List<Object>) field).forEach(o -> {
                resultsList.add(key + "[]=" + o.toString());
            });

            return String.join("&", resultsList);
        }

        //для дат
        if (key.toLowerCase().matches("(.*)(since|until)$")) {
            Integer year = (Integer) ((LinkedHashMap) field).get("year");
            Integer month = (Integer) ((LinkedHashMap) field).get("monthValue");
            Integer day = (Integer) ((LinkedHashMap) field).get("dayOfMonth");

            var date = LocalDate.of(year, Month.of(month), day);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return key + "=" + date.format(formatter);
        }

        return "";
    }
}