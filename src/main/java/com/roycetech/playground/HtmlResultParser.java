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
class HtmlResultParser {

    private transient int totalTestRuns = 0;

    private transient int totalFailures = 0;

    private transient int totalErrors = 0;

    private transient int totalTime = 0;

    /**
     * @param summary in the format: 'Pass: 3 Fail: 0 Errors: 0'
     */
    void computeTotal(final Document doc)
    {
        final Elements titleElement = doc.select("title");
        final String summary = titleElement.text();
        final Pattern pattern =
                Pattern.compile("(?<=Pass|Fail|Errors):\\s(\\d+)");

        final Matcher matcher = pattern.matcher(summary);

        this.totalTestRuns = 0;
        matcher.find();
        this.totalTestRuns += Integer.parseInt(matcher.group(1));
        matcher.find();
        this.totalErrors = Integer.parseInt(matcher.group(1));
        this.totalTestRuns += this.totalErrors;

        matcher.find();
        this.totalFailures = Integer.parseInt(matcher.group(1));
        this.totalTestRuns += this.totalFailures;

        System.out.println("Total: " + this.totalTestRuns);
    }

    /**
     * Global Stats (1567 ms)
     *
     * @param doc
     */
    void extractTestTime(final Document doc)
    {
        final Elements statElement = doc.select("#globalStats h2");
        final String timeText = statElement.text();

        final Pattern pattern = Pattern.compile("(\\d+)(?= ms)");
        final Matcher matcher = pattern.matcher(timeText);

        if (matcher.find()) {
            this.totalTime = Integer.parseInt(matcher.group(1));
        }
    }

    /**
     *
     * @param doc
     */
    String generateSummary(final Document doc)
    {
        if (this.totalErrors + this.totalFailures == 0) {
            return "0,0,0,0";
        }
        return String.valueOf(getTotalTestRuns()) + ',' + getTotalErrors() + ','
                + getTotalFailures() + ',' + getTotalTime();
    }

    int getTotalErrors()
    {
        return this.totalErrors;
    }

    int getTotalFailures()
    {
        return this.totalFailures;
    }

    int getTotalTestRuns()
    {
        return this.totalTestRuns;
    }

    int getTotalTime()
    {
        return this.totalTime;
    }

    public void parse()
    {

    }

    public int parse(final String url) throws IOException
    {
        final Document doc;
        final Connection connection = Jsoup.connect(url);
        doc = connection.get();
        computeTotal(doc);
        extractTestTime(doc);
        return connection.response().statusCode();
    }
}
