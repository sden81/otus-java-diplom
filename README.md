#Обработчик заказов

В okDesk приходят много однотипных заказов на поставку оборудования от клиента. Микросервис делает парсинг заказа и заполняет соответствующие ячейки в google таблицах.

####Порядок работы
1. В окDesk идет запрос задач по признакам:
    * статус задач не должен быть "завершена", "закрыта", "в ленту";
    * от определенного автора (id автора указывается в настройках);
    * за N последних дней (количество дней указывается в настройках).
2. Идет фильтрация задач окDesk на оновании отсутствия маркерного комментария (при успешной обработке задачи вставляется маркерный комментарий "Данные из заявки перенесены в таблицу").
3. Идет обработка оставшихся задач okDesk:
    * из описания задачи парсятся нужные объекты;
    * на основании спарсеных объектов определяется название вкладки в google таблицах;
    * проверяется наличие владки с таким названием и если ее нет, то создается путем копирования владки с названием "sheet_template";
    * на основании колонки "Получение заявки на услугу в Ariba" определяется последний заполненный ряд;
    * идет вставка записи;
    * проверяется, что запись вставилась и после этого задачу оkDesk вставляется маркерный комментарий "Данные из заявки перенесены в таблицу".

Таблица с заказами https://docs.google.com/spreadsheets/d/15xEoH4Gs-mQpfV4A5fJ7Pif0VaFEjZirYBY-Cd69_dA

####Запуск
собираем контейнер
docker-compose build 
запускаем
docker-compose up 

####Куда смотреть
После запуска в таблице гугл появится страница 'Июнь (2020) Атак' с записью о тестовом заказе. 
Эту таблицу можно удалить и при следующем запуске она опять появится.

####Локальный запуск
Нужно запустить jar файл.

java -jar purchase-order-processor-1.0.0.jar

Необязательный ключи:
- issues - дополнительный фильтр по задачам okDesk. Используется при тестировании.
- schedulingInterval - интервал повторения в секундах. При его отсутствии программа отработает одит раз и завершиться.

Например:
 java -jar purchase-order-processor-1.0.0.jar -issues 79754
Запустит один раз и попытается обработать только заявку 79754

 java -jar purchase-order-processor-1.0.0.jar -schedulingInterval 3600
Будет зпускать через каждый час.

 ####Для разработчиков
 Логи пишутся в /logs.
 