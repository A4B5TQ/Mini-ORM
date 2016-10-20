package miniORM.typeDefinition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

    private static final String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ", "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss", "yyyyMMdd", "E MMM dd HH:mm:ss Z yyyy",
            "M/y", "M/d/y", "M-d-y"};

    public static String parseDate(Date date, String outputFormat) {
        if (date != null) {
            return new SimpleDateFormat(outputFormat).format(date);
        }
        return null;
    }

    private static String getFormat(Date date) {
        String currentFormat = date.toString();
        for (String parse : formats) {
            DateFormat dateFormat = new SimpleDateFormat(parse);
            try {
                dateFormat.parse(currentFormat);
                return parse;
            } catch (ParseException ignored) {
            }
        }
        return currentFormat;
    }
}
