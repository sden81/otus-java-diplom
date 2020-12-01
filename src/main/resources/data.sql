INSERT INTO user (id,first_name, last_name, email) VALUES (1,'Piter','Pen', 'email@example.com');
INSERT INTO token (id,user_id, token_string, expired_at) VALUES (1,1,'123','2120-12-31 00:00:00.0 ');

INSERT INTO issue(id, content) VALUES (80475,
'{
    "id": 80475,
    "title": "Покупатель HOLDING отправил новый заказ на закупку EP3269109",
    "description": "<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Sample</title></head><body><div id=\"orderId\">EP3269109</div><div id=\"consumerName\">Магазин№1</div><div id=\"totalSum\">1500</div><div id=\"itemCount\">2(EA)</div><div id=\"reportingDate\">20 июня 2020</div><div id=\"companyName\">Атак</div></body></html>",
    "created_at": "2020-10-16T20:32:39.776",
    "completed_at": "2020-10-17T00:30:36.014",
    "deadline_at": "2020-10-23T13:00:00.000",
    "source": "from_email",
    "spent_time_total": 0.05,
    "start_execution_until": null,
    "planned_execution_in_hours": null,
    "planned_reaction_at": "2020-10-19T11:00:00.000",
    "reacted_at": "2020-10-17T00:30:29.483",
    "updated_at": "2020-10-20T00:47:16.939",
    "delayed_to": null,
    "company_id": 65,
    "group_id": 1,
    "service_object_id": null,
    "equipment_ids": [],
    "attachments": [
        {
            "id": 3909503,
            "attachment_file_name": "EP3269109.htm",
            "description": null,
            "attachment_file_size": 22654,
            "is_public": true,
            "created_at": "2020-10-16T20:32:39.865"
        }
    ],
    "status_times": {
        "opened": {
            "total": "0 д., 3 ч., 57 м.",
            "on_schedule_total": "0 д., 0 ч., 0 м."
        },
        "worked": {
            "total": "0 д., 0 ч., 0 м.",
            "on_schedule_total": "0 д., 0 ч., 0 м."
        },
        "completed": {
            "total": "3 д., 0 ч., 16 м.",
            "on_schedule_total": "0 д., 9 ч., 0 м."
        }
    },
    "parameters": [
        {
            "code": "100",
            "name": "Подтип",
            "field_type": "ftselect",
            "value": "Ariba"
        },
        {
            "code": "name",
            "name": "Имя клиента",
            "field_type": "ftstring",
            "value": ""
        },
        {
            "code": "contract_type",
            "name": "Финансовый тип договора",
            "field_type": "ftselect",
            "value": "Коммерческий"
        }
    ],
    "comments": {
        "count": 1,
        "last_at": "2020-10-17T00:30:36.038"
    },
    "parent_id": null,
    "child_ids": [],
    "type": {
        "id": 14561,
        "code": "service",
        "name": "Обслуживание",
        "available_for_client": true
    },
    "priority": {
        "code": "normal",
        "name": "Обычный"
    },
    "status": {
        "code": "opened",
        "name": "Открыта"
    },
    "old_status": {
        "code": "completed",
        "name": "Решена"
    },
    "rate": {
        "id": 4427663,
        "value": null
    },
    "observers": [
        {
            "id": 7,
            "type": "employee",
            "name": "Алиса"
        },
        {
            "id": 21,
            "type": "employee",
            "name": "Арина"
        }
    ],
    "observer_groups": [],
    "contact": {
        "id": 4654,
        "name": "HOLDING\\\" \\\""
    },
    "agreement": {
        "id": 56,
        "title": "№1/TB от 15.12.2018",
        "start_date": null,
        "end_date": null,
        "cost": 0,
        "crm_1c_id": null,
        "company_ids": [
            65
        ]
    },
    "assignee": {
        "id": 33,
        "name": "1-ая линия поддержки"
    },
    "author": {
        "id": 4654,
        "name": "HOLDING",
        "type": "contact"
    }
}');

INSERT INTO comment (id, issue_id, content) VALUES (8586912, 80475,
 '{
        "id": 8586912,
        "content": "Comment1",
        "public": true,
        "published_at": "2020-10-17T00:30:36.045",
        "author": {
            "id": 42,
            "name": "Марина",
            "type": "employee"
        }
    }
');