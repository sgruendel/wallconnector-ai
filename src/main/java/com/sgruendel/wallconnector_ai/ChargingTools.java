package com.sgruendel.wallconnector_ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;


@Component
public class ChargingTools {

    @Tool(description = "Get the monthly energy total for charging sessions for the given month.")
    public String getMonthlyEnergyTotal(@ToolParam(description = "The month in ISO format.") String month) {

        // Query MongoDB for session data
        // Aggregate energy usage in Wh
        return "In " + month + ", you charged 34.2 kWh across 5 sessions.";
    }

}
