package Tier1.DistributedIdGenerator;

import Tier1.DistributedIdGenerator.service.SnowflakeIdGenerator;

// ---------------------------------------------------------
// Driver Class to Simulate Scenarios
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        // Initialize Generator with Worker ID 1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);

        System.out.println("--- Scenario 1: Single ID ---");
        long id = generator.nextId();
        System.out.println("Generated ID: " + id);
        printBinary(id);

        System.out.println("\n--- Scenario 2: Burst Mode (Same Millisecond) ---");
        // Generate 3 IDs quickly
        for(int i=0; i<3; i++) {
            long burstId = generator.nextId();
            System.out.println("Burst ID: " + burstId + " | Seq: " + (burstId & 0xFFF)); // Mask to show sequence
        }
    }

    private static void printBinary(long id) {
        System.out.println("Binary: " + String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
    }
}

