package ru.timebook.orderhandler.tasks;

import org.apache.commons.cli.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RunOptionsImpl implements RunOptions {
    private CommandLine commandLine;

    public void parseArguments(String[] args) {
        var options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            this.commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
    }

    private Options createOptions() {
        var options = new Options();
        options.addOption(Option.builder("schedulingInterval")
                .argName("seconds")
                .desc("Запускать через каждые n секунд")
                .hasArgs()
                .build());

        options.addOption(Option.builder("issues")
                .argName("okDeskIssueId1,okDeskIssueId2..okDeskIssueIdN")
                .valueSeparator(',')
                .desc("тикеты okDesc которые можем обрабатывать (для тестов)")
                .hasArgs()
                .build());

        options.addOption(Option.builder("generate_token")
                .desc("генерация токена для google spreadsheet")
                .build());

        return options;
    }

    public Optional<Set<Long>> getProcessIssueIdsOnly() {
        String[] processIssueIdsOnlyAsStrings = commandLine.getOptionValues("issues");
        return processIssueIdsOnlyAsStrings == null ? Optional.empty() :
                Optional.of(Arrays.stream(processIssueIdsOnlyAsStrings).map(Long::parseLong)
                        .collect(Collectors.toSet()));
    }

    public Optional<Integer> getSchedulingInterval() {
        String schedulingIntervalAsStrings = commandLine.getOptionValue("schedulingInterval");

        return schedulingIntervalAsStrings == null ? Optional.empty() : Optional.of(Integer.parseInt(schedulingIntervalAsStrings));
    }

    public boolean isGenerateTokenOnly() {
        return commandLine.hasOption("generate_token");
    }
}

