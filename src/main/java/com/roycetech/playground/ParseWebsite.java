/**
 *
 */
package com.roycetech.playground;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author Royce
 */
public class ParseWebsite {

    static int totalTestRuns = 0;
    static int totalFailures = 0;
    static int totalErrors = 0;
    static int totalTime = 0;

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        final Document doc;
        try {
            final Connection connection = Jsoup.connect(
                "http://localhost:8080/testbox/tests/runner.cfm?directory=ecgateway._tests.testbox&recurse=true&reporter=simple&labels=");
            doc = connection.get();

            final Elements titleElement = doc.select("title");
            System.out.println(titleElement.text());

            printTotal(doc);
            printTestTime(doc);
            printSummary(doc);
            System.out
                .println("Status Code: " + connection.response().statusCode());

            System.out.print(connection.response().body().trim());

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param doc
     */
    static void printSummary(final Document doc)
    {
        System.out.println(
            "Summary: " + totalTestRuns + ',' + totalErrors + ','
                    + totalFailures + ',' + totalTime);
    }

    /**
     * Global Stats (1567 ms)
     *
     * @param doc
     */
    static void printTestTime(final Document doc)
    {
        final Elements statElement = doc.select("#globalStats h2");
        final String timeText = statElement.text();

        final Pattern pattern = Pattern.compile("(\\d+)(?= ms)");
        final Matcher matcher = pattern.matcher(timeText);

        if (matcher.find()) {
            totalTime = Integer.parseInt(matcher.group(1));
            System.out.println("Time: " + totalTime);
        }
    }

    /**
     * @param summary in the format: 'Pass: 3 Fail: 0 Errors: 0'
     */
    static void printTotal(final Document doc)
    {
        final Elements titleElement = doc.select("title");
        final String summary = titleElement.text();
        final Pattern pattern =
                Pattern.compile("(?<=Pass|Fail|Errors):\\s(\\d+)");
        final Matcher matcher = pattern.matcher(summary);

        totalTestRuns = 0;
        matcher.find();
        totalTestRuns += Integer.parseInt(matcher.group(1));
        matcher.find();
        totalErrors = Integer.parseInt(matcher.group(1));
        totalTestRuns += totalErrors;

        matcher.find();
        totalFailures = Integer.parseInt(matcher.group(1));
        totalTestRuns += totalFailures;

        System.out.println("Total: " + totalTestRuns);
    }
}
