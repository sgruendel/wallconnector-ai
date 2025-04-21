package com.sgruendel.wallconnector_ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class ChargingTools {

    private final WallconnectorService wallconnectorService;

    @Tool(description = "Get the monthly energy total in wH for charging sessions for the given month.")
    public String getTotalEnergyInWhForMonth(@ToolParam(description = "The month in ISO format like 2024-12.") String month) {

        final Double totalEnergyInWh = wallconnectorService.getTotalEnergyInWhForMonth(month);
        if (totalEnergyInWh == null) {
            return "no data for month " + month;
        }
        return "total energy usage in " + month + " was " + totalEnergyInWh + " Wh";
    }

    @Tool(description = "Get the list of daily number of charging sessions and their duration in seconds for the given month.")
    public String getDailyChargingSessionsForMonth(@ToolParam(description = "The month in ISO format like 2024-12.") String month) {

        final String dailyChargingSessions = wallconnectorService.getDailyChargingSessionsForMonth(month);
        return "list of daily charging sessions for " + month + ": " + dailyChargingSessions;
    }

    @Tool(description = "Get the charging session with the longest duration.")
    public String getLongestChargingSession() {

        final String chargingSession = wallconnectorService.getLongestChargingSession();
        return "longest charging session: " + chargingSession;
    }

    @Tool(description = "Get the charging session with the shortest duration.")
    public String getShortestChargingSession() {

        final String chargingSession = wallconnectorService.getShortestChargingSession();
        return "shortest charging session: " + chargingSession;
    }

}
