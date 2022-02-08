package helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.sm.in_and.ChartOptions;
import com.sm.in_and.Options;
import com.sm.in_and.SortOptions;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionsHelper {

    public static List<String> periods = Arrays.asList("-30", "-60", "-90", "30", "other", "all");
    public static List<String> viewScopeValues = Arrays.asList("all", "inc", "exp");
    public static List<String> viewTypeValues = Arrays.asList("det", "cat");

    public static List<String> periodsText = Arrays.asList("Ostatnie 30 dni", "Ostatnie 60 dni", "Ostatnie 90 dni", "Najbliższe 30 dni", "Inny okres", "Wszystko");

    public static List<String> chartTypeValues = Arrays.asList("bartotals", "pieinc", "pieexp");

    public static LocalDate parseDate(String dateToParse){
        try{
            return LocalDate.parse(dateToParse);
        }catch (Exception e){
            return null;
        }
    }

    public static Integer parseInt(String valueToParse) {
        try {
            return Integer.parseInt(valueToParse);
        } catch (Exception e) {
            return null;
        }
    }

    public static Options verifyOptions (Options options, String viewType){

        if(!periods.contains(options.getPeriod())){
            options.setPeriod("-30");
        }

        Integer period = parseInt(options.getPeriod());
        if(period != null){
            if(period > 0){
                if(!options.getStartDate().equals(LocalDate.now()) || !options.getEndDate().equals(LocalDate.now().plus(period, ChronoUnit.DAYS))){
                    options.setStartDate(LocalDate.now());
                    options.setEndDate(LocalDate.now().plus(period, ChronoUnit.DAYS));
                }
            }else{
                if(!options.getStartDate().equals(LocalDate.now().plus(period,ChronoUnit.DAYS)) || !options.getEndDate().equals(LocalDate.now())){
                    options.setStartDate(LocalDate.now().plus(period, ChronoUnit.DAYS));
                    options.setEndDate(LocalDate.now());
                }
            }
        }else{
            if(options.getStartDate() == null || options.getEndDate() == null){
                options.setStartDate(LocalDate.now().plus(-30, ChronoUnit.DAYS));
                options.setEndDate(LocalDate.now());
            }else{
                if(options.getStartDate().isAfter(options.getEndDate())){
                    options.setStartDate(options.getEndDate());
                }
            }
        }

        if(!viewScopeValues.contains(options.getViewScope())){
            options.setViewScope("all");
        }

        if(!viewTypeValues.contains(options.getViewType())){
            options.setViewType(viewType);
        }

        return options;
    }

    public static Options getOptions(Context context, String currentViewType){
        Options options = new Options();
        SharedPreferences viewOptions = context.getSharedPreferences("ViewOptions",context.MODE_PRIVATE);
        options.setPeriod(viewOptions.getString("period", "-30"));
        options.setStartDate(parseDate(viewOptions.getString("startdate", String.format("%tF", LocalDate.now().minus(30, ChronoUnit.DAYS)))));
        options.setEndDate(parseDate(viewOptions.getString("enddate", String.format("%tF", LocalDate.now()))));
        options.setViewScope(viewOptions.getString("viewscope", "all"));
        options.setViewType(viewOptions.getString("viewtype", currentViewType));
        return verifyOptions(options, currentViewType);
    }

    public static void setOptions(Context context, String period, String startDate, String endDate, String viewScope, String viewType){
        SharedPreferences viewOptions = context.getSharedPreferences("ViewOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor viewOptionsEdit = viewOptions.edit();
        viewOptionsEdit.putString("period", period);
        viewOptionsEdit.putString("startdate", startDate);
        viewOptionsEdit.putString("enddate", endDate);
        viewOptionsEdit.putString("viewscope", viewScope);
        viewOptionsEdit.putString("viewtype", viewType);
        viewOptionsEdit.commit();
    }

    public static void clearOptions(Context context){
        SharedPreferences viewOptions = context.getSharedPreferences("ViewOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor viewOptionsEdit = viewOptions.edit();
        viewOptionsEdit.clear();
        viewOptionsEdit.commit();
    }

    public static SortOptions getSortOptions(Context context, String viewType){
        SortOptions sortOptions = new SortOptions();
        SharedPreferences setSortOptions = context.getSharedPreferences("SortOptions",context.MODE_PRIVATE);
        if(viewType.equals("cat")){
            sortOptions.setSortBy(setSortOptions.getString("sortby", "categoryname"));
        }else{
            sortOptions.setSortBy(setSortOptions.getString("sortby", "date"));
        }
        sortOptions.setSortType(setSortOptions.getString("sorttype", "asc"));
        return sortOptions;
    }

    public static void setSortOptions(Context context, String sortBy, String sortType){
        SharedPreferences setSortOptions = context.getSharedPreferences("SortOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor setSortOptionsEdit = setSortOptions.edit();
        setSortOptionsEdit.putString("sortby", sortBy);
        setSortOptionsEdit.putString("sorttype", sortType);
        setSortOptionsEdit.commit();
    }

    public static void clearSortOptions(Context context){
        SharedPreferences setSortOptions = context.getSharedPreferences("SortOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor setSortOptionsEdit = setSortOptions.edit();
        setSortOptionsEdit.clear();
        setSortOptionsEdit.commit();
    }

    public static ChartOptions verifyChartOptions (ChartOptions chartOptions){

        if(!periods.contains(chartOptions.getPeriod())){
            chartOptions.setPeriod("-30");
        }

        Integer period = parseInt(chartOptions.getPeriod());
        if(period != null){
            if(period > 0){
                if(!chartOptions.getStartDate().equals(LocalDate.now()) || !chartOptions.getEndDate().equals(LocalDate.now().plus(period, ChronoUnit.DAYS))){
                    chartOptions.setStartDate(LocalDate.now());
                    chartOptions.setEndDate(LocalDate.now().plus(period, ChronoUnit.DAYS));
                }
            }else{
                if(!chartOptions.getStartDate().equals(LocalDate.now().plus(period,ChronoUnit.DAYS)) || !chartOptions.getEndDate().equals(LocalDate.now())){
                    chartOptions.setStartDate(LocalDate.now().plus(period, ChronoUnit.DAYS));
                    chartOptions.setEndDate(LocalDate.now());
                }
            }
        }else{
            if(chartOptions.getStartDate() == null || chartOptions.getEndDate() == null){
                chartOptions.setStartDate(LocalDate.now().plus(-30, ChronoUnit.DAYS));
                chartOptions.setEndDate(LocalDate.now());
            }else{
                if(chartOptions.getStartDate().isAfter(chartOptions.getEndDate())){
                    chartOptions.setStartDate(chartOptions.getEndDate());
                }
            }
        }

        if(!chartTypeValues.contains(chartOptions.getChartType())){
            chartOptions.setChartType("bartotals");
        }

        return chartOptions;
    }

    public static ChartOptions getChartOptions(Context context){
        ChartOptions chartOptions = new ChartOptions();
        SharedPreferences setChartOptions = context.getSharedPreferences("ChartOptions",context.MODE_PRIVATE);
        chartOptions.setPeriod(setChartOptions.getString("period", "-30"));
        chartOptions.setStartDate(parseDate(setChartOptions.getString("startdate", String.format("%tF", LocalDate.now().minus(30, ChronoUnit.DAYS)))));
        chartOptions.setEndDate(parseDate(setChartOptions.getString("enddate", String.format("%tF", LocalDate.now()))));
        chartOptions.setChartType(setChartOptions.getString("charttype", "bartotals"));
        return verifyChartOptions(chartOptions);
    }

    public static void setChartOptions(Context context, String period, String startDate, String endDate, String chartType){
        SharedPreferences setChartOptions = context.getSharedPreferences("ChartOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor setChartOptionsEdit = setChartOptions.edit();
        setChartOptionsEdit.putString("period", period);
        setChartOptionsEdit.putString("startdate", startDate);
        setChartOptionsEdit.putString("enddate", endDate);
        setChartOptionsEdit.putString("charttype", chartType);
        setChartOptionsEdit.commit();
    }

    public static void clearChartOptions(Context context){
        SharedPreferences setChartOptions = context.getSharedPreferences("ChartOptions",context.MODE_PRIVATE);
        SharedPreferences.Editor setChartOptionsEdit = setChartOptions.edit();
        setChartOptionsEdit.clear();
        setChartOptionsEdit.commit();
    }
}
