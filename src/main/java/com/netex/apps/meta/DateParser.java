package com.netex.apps.meta;


import fj.F;
import fj.data.Option;
import fj.data.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fj.Function.curry;
import static fj.data.Option.*;
import static fj.data.Stream.stream;

public class DateParser {

    private static final Logger logger = LogManager.getLogger(DateParser.class);


    public static F<String, F<String, Option<Date>>> parseDate =
        curry((pattern, s) -> {
            try {
                return some(new SimpleDateFormat(pattern).parse(s));
            } catch (ParseException e) {
                return none();
            }
        });

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
        "yyyyMMdd'T'HH:mm:ss",
        "dd/MM/yy'T'HH:mm:ss",
        "dd/MM/yyyy'T'HH:mm:ss",
        "MM/dd/yy'T'HH:mm:ss",
        "MM/dd/yyyy'T'HH:mm:ss",
        "MM-dd-yy'T'HH:mm:ss",
        "MM-dd-yyyy'T'HH:mm:ss",
        "yyyyMMdd",
        "dd/MM/yy",
        "dd/MM/yyyy",
        "MM/dd/yy",
        "MM/dd/yyyy",
        "MM-dd-yy",
        "MM-dd-yyyy"
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

    public static Option<Option<Date>> parseWithPatterns(String s) {
        return parseWithPatterns(s, stream(DATE_PATTERNS));
    }

    public static Option<Option<Date>> parseWithPatterns(String s, Stream<String> patterns) {
        return stream(s).apply(patterns.map(parseDate)).find(isSome_());
    }
}
