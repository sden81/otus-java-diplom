package ru.timebook.orderhandler.okDeskClient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Issue {
    long id;

    String title;

    String description;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime createdAt;

    @JsonProperty("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonProperty("deadline_at")
    LocalDateTime deadlineAt;

    String source;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime updatedAt;

    @JsonProperty("company_id")
    long companyId;

    @JsonProperty("group_id")
    long groupId;

    Status status;

    @JsonProperty("old_status")
    Status oldStatus;

    Author author;


    //            "delayed_to":null,
    //            "spent_time_total":0.1,
//            "start_execution_until":null,
//            "planned_execution_in_hours":null,
//            "planned_reaction_at":"2020-09-18T11:12:00.000+03:00",
//            "reacted_at":"2020-09-18T11:08:47.724+03:00",

//            "service_object_id":1422,
//            "equipment_ids":[
//
//   ],
//        "attachments":[
//        {
//            "id":3668397,
//                "attachment_file_name":"image001.png",
//                "description":null,
//                "attachment_file_size":6643,
//                "is_public":true,
//                "created_at":"2020-09-18T10:42:45.460+03:00"
//        }
//   ],
//        "status_times":{
//        "opened":{
//            "total":"0 д., 0 ч., 26 м.",
//                    "on_schedule_total":"0 д., 0 ч., 26 м."
//        },
//        "add_info":{
//            "total":"2 д., 9 ч., 40 м.",
//                    "on_schedule_total":"2 д., 9 ч., 38 м."
//        }
//    },
//        "parameters":[
//        {
//            "code":"100",
//                "name":"Подтип",
//                "field_type":"ftselect",
//                "value":"Планирование"
//        },
//        {
//            "code":"name",
//                "name":"Имя клиента",
//                "field_type":"ftstring",
//                "value":""
//        },
//        {
//            "code":"contract_type",
//                "name":"Финансовый тип договора",
//                "field_type":"ftselect",
//                "value":"Коммерческий"
//        }
//   ],
//        "comments":{
//        "count":1,
//                "last_at":"2020-09-18T11:08:47.774+03:00"
//    },
//        "parent_id":null,
//            "child_ids":[
//
//   ],
//        "type":{
//        "id":14561,
//                "code":"service",
//                "name":"Обслуживание",
//                "available_for_client":true
//    },
//        "priority":{
//        "code":"normal",
//                "name":"Обычный"
//    },
//        "rate":{
//        "id":4230234,
//                "value":null
//    },
//        "observers":[
//        {
//            "id":8,
//                "type":"employee",
//                "name":"Администратор Администратор"
//        },
//        {
//            "id":21,
//                "type":"employee",
//                "name":"Романова Арина"
//        },
//        {
//            "id":23,
//                "type":"employee",
//                "name":"Мигунова Нина Валерьевна"
//        },
//        {
//            "id":24,
//                "type":"employee",
//                "name":"Саралидзе Алина Васильевна"
//        },
//        {
//            "id":25,
//                "type":"employee",
//                "name":"Маслова Татьяна"
//        },
//        {
//            "id":36,
//                "type":"employee",
//                "name":"Яковлев Роман"
//        },
//        {
//            "id":42,
//                "type":"employee",
//                "name":"Курикова Марина"
//        },
//        {
//            "id":47,
//                "type":"employee",
//                "name":"Родионова Алина Сергеевна"
//        }
//   ],
//        "observer_groups":[
//
//   ],
//        "contact":{
//        "id":2264,
//                "name":"1646 Магазин"
//    },
//        "agreement":{
//        "id":86,
//                "title":"594/ТВ от 01 апреля 2019",
//                "start_date":null,
//                "end_date":null,
//                "cost":0.0,
//                "crm_1c_id":null,
//                "company_ids":[
//        320
//      ]
//    },
//        "assignee":{
//        "id":21,
//                "name":"Романова Арина"
//    },
}
