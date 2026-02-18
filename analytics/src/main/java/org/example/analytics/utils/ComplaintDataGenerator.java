package org.example.analytics.utils;

import tools.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class ComplaintDataGenerator {

    public static void main(String[] args) throws Exception {

        List<Map<String, Object>> complaints = new ArrayList<>();
        Random random = new Random();

        String[] categories = {"CARPENTRY","ELECTRICAL","PLUMBING","RAGGING"};
        String[] messageTypes = {"GRIEVANCE","ASSISTANCE","ENQUIRY","FEEDBACK","POSITIVE_FEEDBACK"};
        String[] statuses = {"OPEN","IN_PROGRESS","RESOLVED"};
        String[] blocks = {"A1","B2","C1","D1"};

        for(int i = 1; i <= 250; i++) {

            Map<String, Object> complaint = new HashMap<>();

            complaint.put("assigned_to", "Tech" + (random.nextInt(4) + 1));
            complaint.put("availability_date", "2025-01-" + String.format("%02d", random.nextInt(28)+1));
            complaint.put("block", blocks[random.nextInt(blocks.length)]);
            complaint.put("category", categories[random.nextInt(categories.length)]);
            complaint.put("contact_no", "90000" + (1000+i));
            complaint.put("created_at", LocalDateTime.of(2025, random.nextInt(3)+1, random.nextInt(28)+1, random.nextInt(24), random.nextInt(60)).toString());
            complaint.put("description", "Auto generated complaint " + i);
            complaint.put("message_type", messageTypes[random.nextInt(messageTypes.length)]);
            complaint.put("room_no", String.valueOf(100 + random.nextInt(400)));
            complaint.put("room_type", random.nextBoolean() ? "SINGLE" : "DOUBLE");
            complaint.put("specific_category", "General");
            complaint.put("status", statuses[random.nextInt(statuses.length)]);
            complaint.put("sub_block", "A");
            complaint.put("sub_category", "General");
            complaint.put("time_slot", "MORNING");
            complaint.put("raised_by", random.nextInt(30)+1);

            complaints.add(complaint);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("complaints_seed.json"), complaints);

        System.out.println("Generated 250 complaints successfully.");
    }
}
