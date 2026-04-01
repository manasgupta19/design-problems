package MachineCoding.TravelItinerary;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MachineCoding.TravelItinerary.gateway.MockFlightProvider;
import MachineCoding.TravelItinerary.gateway.MockHotelProvider;
import MachineCoding.TravelItinerary.gateway.MockTrainProvider;
import MachineCoding.TravelItinerary.model.ItineraryContext;
import MachineCoding.TravelItinerary.service.BookingStep;
import MachineCoding.TravelItinerary.service.FlightBookingStep;
import MachineCoding.TravelItinerary.service.HotelBookingStep;
import MachineCoding.TravelItinerary.service.ItineraryOrchestrator;
import MachineCoding.TravelItinerary.service.TrainBookingStep;

public class SagaDriver {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Travel Itinerary Saga...");

        MockFlightProvider flightProvider = new MockFlightProvider();
        MockHotelProvider hotelProvider = new MockHotelProvider();
        MockTrainProvider trainProvider = new MockTrainProvider();

        List<BookingStep> itinerarySteps = Arrays.asList(
                new FlightBookingStep(flightProvider),
                new HotelBookingStep(hotelProvider),
                new TrainBookingStep(trainProvider)
        );

        ItineraryOrchestrator orchestrator = new ItineraryOrchestrator(itinerarySteps);

        int concurrentUsers = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch latch = new CountDownLatch(concurrentUsers);

        for (int i = 1; i <= concurrentUsers; i++) {
            final String userId = "USER_" + i;
            executorService.submit(() -> {
                try {
                    String itineraryId = "ITINERARY_" + UUID.randomUUID().toString().substring(0,5);
                    ItineraryContext context = new ItineraryContext(itineraryId, userId);
                    System.out.println("Starting booking for itinerary: " + itineraryId + ", user: " + userId);
                    orchestrator.bookItinerary(context);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        System.out.println("All itinerary bookings processed.");
    }
}
