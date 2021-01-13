package ru.timebook.orderhandler;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.timebook.orderhandler.healthcheck.HealthCheckService;
import ru.timebook.orderhandler.healthcheck.items.GoogleSpreadsheetIntegrationCheck;
import ru.timebook.orderhandler.healthcheck.items.HealthCheckItem;
import ru.timebook.orderhandler.healthcheck.items.OkDeskIntegrationCheck;
import ru.timebook.orderhandler.okDeskClient.IssueListFilter;
import ru.timebook.orderhandler.okDeskClient.dto.StatusCodes;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ComponentScan
@Configuration
public class Config {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, @Value("${okDesk.uri}") String uri) {
        return restTemplateBuilder
                .rootUri(uri)
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Список открытых заявок на закупку считывателей от Ашана
     *
     * @param sinceDaysAgo
     * @return
     */
    @Bean
    public IssueListFilter getStandardIssueListFilter(
            @Value("${okDesk.getIssuesSinceDaysAgo}") Integer sinceDaysAgo,
            @Value("${okDesk.okDeskAuthorId}") Integer okDeskAuthorId
    ) {
        return IssueListFilter.builder()
                .created_since(LocalDate.now().minusDays(sinceDaysAgo))
                .addAuthorId(okDeskAuthorId)
                .addStatusNot(StatusCodes.closed)
                .addStatusNot(StatusCodes.completed)
                .addStatusNot(StatusCodes.to_lenta)
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                                .expireAfterWrite(1, TimeUnit.HOURS)
                                .build().asMap(),
                        false);
            }
        };
    }

    @Bean
    public Set<HealthCheckItem> getHealthCheckItems(
            GoogleSpreadsheetIntegrationCheck googleSpreadsheetIntegrationCheck,
            OkDeskIntegrationCheck okDeskIntegrationCheck
    ) {
        var items = new HashSet<HealthCheckItem>();
        items.add(googleSpreadsheetIntegrationCheck);
        items.add(okDeskIntegrationCheck);

        return items;
    }
}
