package com.sgruendel.wallconnector_ai;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WallconnectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WallconnectorService.class);

    private final MongoClient mongoClient;

    private MongoDatabase mongoDatabase;

    @PostConstruct
    public void init() {
        // Initialize the MongoClient or any other resources if needed
        LOGGER.info("ChargingTools initialized with MongoClient {}", mongoClient);
        /*
        mongoClient.listDatabases()
                .forEach(db -> System.out.println("Database: " + db.toString()));
        final MongoDatabase db = mongoClient.getDatabase("tesla-wallconnector");
        */

        mongoDatabase = mongoClient.getDatabase("tesla-wallconnector");
        LOGGER.info("ChargingTools initialized with MongoDatabase {}", mongoDatabase);
    }

    public Double getTotalEnergyInWhForMonth(final String month) {

        // Parse month string (e.g. "2024-12") to create start and end dates
        LocalDateTime startDate = LocalDateTime.parse(month + "-01T00:00:00");
        LocalDateTime endDate = startDate.plusMonths(1);

        LOGGER.debug("Querying total energy in Wh for dates between {} and {}", startDate, endDate);

        final var sum = mongoDatabase.getCollection("sessions")
            .aggregate(List.of(
                Aggregates.match(
                    Filters.and(
                        Filters.gte("start_date", startDate),
                        Filters.lt("start_date", endDate)
                    )
                ),
                Aggregates.group(null, Accumulators.sum("total_energy", "$session_energy_wh"))
            ))
            .first();

        if (sum == null) {
            LOGGER.info("No data found for month {}", month);
            return null;
        }

        final var totalEnergy = sum.getDouble("total_energy");
        LOGGER.info("Total energy for month {}: {}", month, totalEnergy);
        return totalEnergy;
    }

    public String getDailyChargingSessionsForMonth(final String month) {

        // Parse month string (e.g. "2024-12") to create start and end dates
        LocalDateTime startDate = LocalDateTime.parse(month + "-01T00:00:00");
        LocalDateTime endDate = startDate.plusMonths(1);

        LOGGER.debug("Querying daily charging sessions for dates between {} and {}", startDate, endDate);

        final List<Document> results = mongoDatabase.getCollection("sessions")
            .aggregate(List.of(
                Aggregates.match(
                    Filters.and(
                        Filters.gte("start_date", startDate),
                        Filters.lt("start_date", endDate)
                    )
                ),
                Aggregates.project(
                    new Document("date",
                        new Document("$dateToString",
                            new Document("format", "%Y-%m-%d")
                            .append("date", "$start_date")
                        ))
                    .append("duration", "$uptime_s")
                ),
                Aggregates.sort(new Document("date", 1))
            ))
            .into(new ArrayList<>());

        if (results.isEmpty()) {
            LOGGER.info("No charging sessions found for month {}", month);
            return "No charging sessions found";
        }

        // Format result as string with date and session durations
        final String result = results.stream()
            .map(doc -> String.format("%s: %d seconds",
                doc.getString("date"),
                doc.getInteger("duration")))
            .collect(Collectors.joining("\n"));

        LOGGER.info("Charging sessions for month {}: {}", month, result);
        return result;
    }

    public String getLongestChargingSession() {

        LOGGER.debug("Querying longest charging sessions");

        final List<Document> results = mongoDatabase.getCollection("sessions")
            .aggregate(List.of(
                Aggregates.sort(new Document("uptime_s", -1)),
                Aggregates.limit(1),
                Aggregates.project(
                    new Document("start_date", "$start_date")
                    .append("end_date", "$end_date")
                    .append("duration", "$uptime_s")
                    .append("energy_wh", "$session_energy_wh")
                )
            ))
            .into(new ArrayList<>());

        if (results.isEmpty()) {
            LOGGER.info("No charging session found");
            return "No charging session found";
        }

        // Format result as string with date and session durations
        final String result = results.stream()
            .map(doc -> String.format("%s - %s: duration %d seconds, charged %d Wh",
                doc.getDate("start_date"),
                doc.getDate("end_date"),
                doc.getInteger("duration"),
                doc.getInteger("energy_wh")))
            .collect(Collectors.joining(", "));

        LOGGER.info("Longest charging session: {}", result);
        return result;
    }

    public String getShortestChargingSession() {

        LOGGER.debug("Querying longest charging sessions");

        final List<Document> results = mongoDatabase.getCollection("sessions")
            .aggregate(List.of(
                Aggregates.sort(new Document("uptime_s", 1)),
                Aggregates.limit(1),
                Aggregates.project(
                    new Document("start_date", "$start_date")
                    .append("end_date", "$end_date")
                    .append("duration", "$uptime_s")
                    .append("energy_wh", "$session_energy_wh")
                )
            ))
            .into(new ArrayList<>());

        if (results.isEmpty()) {
            LOGGER.info("No charging session found");
            return "No charging session found";
        }

        // Format result as string with date and session durations
        final String result = results.stream()
            .map(doc -> String.format("%s - %s: duration %d seconds, charged %d Wh",
                doc.getDate("start_date"),
                doc.getDate("end_date"),
                doc.getInteger("duration"),
                doc.getInteger("energy_wh")))
            .collect(Collectors.joining(", "));

        LOGGER.info("shortest charging session: {}", result);
        return result;
    }

}
