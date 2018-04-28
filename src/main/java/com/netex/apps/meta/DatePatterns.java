package com.netex.apps.meta;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePatterns {

    private static final Logger logger = LogManager.getLogger(DatePatterns.class);

    public final static String[] DATE_PATTERNS = {
            "yyyy.MM.dd G 'at' HH:mm:ss z",
            "EEE, MMM d, ''yy",
            "h:mm a",
            "hh 'o''clock' a, zzzz",
            "K:mm a, z",
            "yyyyy.MMMMM.dd GGG hh:mm aaa",
            "EEE, d MMM yyyy HH:mm:ss Z",
            "yyMMddHHmmssZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "YYYY-'W'ww-u",
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, dd MMM yyyy HH:mm zzzz",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSzzzz",
            "yyyy-MM-dd'T'HH:mm:sszzzz",
            "yyyy-MM-dd'T'HH:mm:ss z",
            "yyyy-MM-dd'T'HH:mm:ssz",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HHmmss.SSSz",
            "yyyy-MM-dd",
            "yyyyMMdd",
            "dd/MM/yy",
            "dd/MM/yyyy"
    };

    private static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd G 'at' HH:mm:ss z"))
            .appendOptional(DateTimeFormatter.ofPattern("EEE, MMM d, ''yy"))
            .appendOptional(DateTimeFormatter.ofPattern("h:mm a"))
            .appendOptional(DateTimeFormatter.ofPattern("hh 'o''clock' a, zzzz"))
            .appendOptional(DateTimeFormatter.ofPattern("K:mm a, z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyyy.MMMMM.dd GGG hh:mm aaa"))
            .appendOptional(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyMMddHHmmssZ"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
            .appendOptional(DateTimeFormatter.ofPattern("YYYY-'W'ww-u"))
            .appendOptional(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))
            .appendOptional(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm zzzz"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSzzzz"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:sszzzz"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss.SSSz"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toFormatter();

    private static DateTimeFormatter buildFormatter() {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        Arrays.stream(DATE_PATTERNS).forEach(
                pattern -> builder.appendOptional(DateTimeFormatter.ofPattern(pattern))
        );
        return builder.toFormatter();
    }

    public static boolean isParsable(String date) {
        boolean result = true;
        try {
            buildFormatter().parse(date);
        } catch (DateTimeParseException e) {
            logger.error(e.getCause().getMessage());
            result = false;
        } finally {
            return result;
        }
    }

    public static boolean isDate(String value) {
        String pattern = "\\d{4}-[01]\\d-[0-3]\\d\\s[0-2]\\d((:[0-5]\\d)?){2}";
        Pattern checker = Pattern.compile(pattern);
        Matcher matcher = checker.matcher(value);
        return matcher.find();
    }
}
