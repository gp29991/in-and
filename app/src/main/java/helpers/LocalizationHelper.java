package helpers;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

public class LocalizationHelper {

    public static String getLocalizedDate(String input){
        LocalDate date;
        String dateString = input.substring(0,10);
        try {
            date = LocalDate.parse(dateString);
        }catch (Exception e){
            return null;
        }
        Locale locale = new Locale("pl", "PL");
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);
        pattern = pattern.replace("yy", "yyyy");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(dateFormatter);
    }

    public static String getLocalizedDate(LocalDate input){
        Locale locale = new Locale("pl", "PL");
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);
        pattern = pattern.replace("yy", "yyyy");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        return input.format(dateFormatter);
    }

}
