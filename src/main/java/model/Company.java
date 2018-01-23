package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

/**
 * A company need only have it's name, current and older statistical information ({@link Stats}) and whether it's currently live (means someone has traded on it for the current day).
 */
public class Company
{
    private String name;
    private Stats yesterday;
    private Stats today;

    @JsonCreator
    public Company(@JsonProperty("name") String name,
                   @JsonProperty("yesterday") Stats yesterday,
                   @JsonProperty("today") Stats today)
    {
        this.name = name;
        this.yesterday = yesterday;
        this.today = today;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }


    @JsonProperty
    public boolean isLive()
    {
        return new Interval(new DateTime(new java.util.Date()).withTime(9,0,0,0),
                            new DateTime(new java.util.Date()).withTime(17,0,0,0))
                       .containsNow();
    }


    @JsonProperty
    public Stats getYesterday()
    {
        return yesterday;
    }

    @JsonProperty
    public Stats getToday()
    {
        return today;
    }

    @JsonProperty
    public Stats getStats(boolean current) {
        Stats result;
        if(current) {
            return today;
        }
        else
            return yesterday;
    }

    @JsonProperty
    public CompanyDay createDay(boolean current)
    {
        if(current) {
            return new CompanyDay(name, getToday());
        }
        else{
            return new CompanyDay(name, getYesterday());
        }
    }

    public void updateStats(TradeCompleted tc)
    {
        if(isLive()) {
            if (today.getHighPeak()
                     .getPeakPricePerStock() > tc.getPeak())
            {
                today.registerPeakLow(tc.getPeak(), tc.getTimestamp());
            }
            else
            {
                today.registerPeakHigh(tc.getPeak(), tc.getTimestamp());
            }
        }
    }
}
