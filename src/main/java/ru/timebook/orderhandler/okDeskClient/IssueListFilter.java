package ru.timebook.orderhandler.okDeskClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import ru.timebook.orderhandler.okDeskClient.dto.StatusCodes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Value
public class IssueListFilter {
    @Singular("addStatus")
    List<StatusCodes> status;
    @Singular("addStatusNot")
    List<StatusCodes> status_not;
    LocalDate created_since;
    LocalDate created_until;
    LocalDate updated_since;
    LocalDate updated_until;
    @Singular("addAuthorId")
    List<Integer> author_contact_ids;

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

            return key + "=" +
                    ((day > 9) ? day.toString() : "0" + day.toString()) + "-" +
                    ((month > 9) ? month.toString() : "0" + month.toString()) + "-" +
                    year.toString();
        }

        return "";
    }
}