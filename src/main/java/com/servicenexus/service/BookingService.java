
package com.servicenexus.service;

import com.servicenexus.dto.BookingResponse;
import com.servicenexus.dto.CreateBookingRequest;
import com.servicenexus.dto.CustomerDashboardResponse;
import com.servicenexus.entity.Customer;
import com.servicenexus.model.Booking;
import com.servicenexus.model.BookingStatus;
import com.servicenexus.model.WorkerService;
import com.servicenexus.repository.BookingRepository;
import com.servicenexus.repository.CustomerRepository;
import com.servicenexus.repository.WorkerServiceRepository;
import com.servicenexus.dto.AvailableServiceResponse;
import com.servicenexus.dto.CustomerDashboardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final WorkerServiceRepository workerServiceRepository;

    public BookingService(
            BookingRepository bookingRepository,
            CustomerRepository customerRepository,
            WorkerServiceRepository workerServiceRepository) {

        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.workerServiceRepository = workerServiceRepository;
    }

    // CREATE CUSTOMER BOOKING
    @Transactional
    public BookingResponse createBooking(
            CreateBookingRequest request,
            String authenticatedEmail) {

        Customer customer = customerRepository
                .findByEmailIgnoreCase(authenticatedEmail.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        WorkerService service = workerServiceRepository
                .findById(request.serviceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Selected service not found"
                ));

        if (!service.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This service is currently unavailable"
            );
        }

        if (request.bookingDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking date cannot be in the past"
            );
        }

        if (request.bookingDate().equals(LocalDate.now())
                && request.preferredTime().isBefore(LocalTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a future time"
            );
        }

        Booking booking = new Booking();

        booking.setCustomer(customer);
        booking.setService(service);

        // Name and email always come from the authenticated profile
        booking.setCustomerName(customer.getName());
        booking.setCustomerEmail(customer.getEmail());

        // Phone and address can be customized for this booking
        booking.setCustomerPhone(request.customerPhone().trim());
        booking.setServiceAddress(request.serviceAddress().trim());

        booking.setBookingDate(request.bookingDate());
        booking.setPreferredTime(request.preferredTime());
        booking.setDescription(request.description().trim());

        // Save the current service price as a booking snapshot
        booking.setPrice(service.getPrice());

        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        return toResponse(savedBooking);
    }

    // GET LOGGED-IN CUSTOMER BOOKING HISTORY
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String authenticatedEmail) {

        Customer customer = customerRepository
                .findByEmailIgnoreCase(authenticatedEmail.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        return bookingRepository
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
public CustomerDashboardResponse getCustomerDashboard(String authenticatedEmail) {

    Customer customer = customerRepository
            .findByEmailIgnoreCase(authenticatedEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Customer not found"));

    Long customerId = customer.getId();

    return new CustomerDashboardResponse(
            bookingRepository.countByCustomerId(customerId),
            bookingRepository.countByCustomerIdAndStatus(
                    customerId, BookingStatus.COMPLETED),
            bookingRepository.countByCustomerIdAndStatus(
                    customerId, BookingStatus.PENDING),
            bookingRepository.countByCustomerIdAndStatus(
                    customerId, BookingStatus.ACCEPTED),
            bookingRepository.countByCustomerIdAndStatus(
                    customerId, BookingStatus.CANCELLED),
            bookingRepository.countByCustomerIdAndStatus(
                    customerId, BookingStatus.REJECTED)
    );
}
    @Transactional
public BookingResponse cancelBooking(Long bookingId, String authenticatedEmail) {

    Customer customer = customerRepository
            .findByEmailIgnoreCase(authenticatedEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Customer not found"));

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Booking not found"));

    if (!booking.getCustomer().getId().equals(customer.getId())) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "You cannot cancel this booking");
    }

    if (booking.getStatus() != BookingStatus.PENDING) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only pending bookings can be cancelled");
    }

    booking.setStatus(BookingStatus.CANCELLED);

    Booking cancelledBooking = bookingRepository.save(booking);

    return toResponse(cancelledBooking);
}
    // CONVERT BOOKING ENTITY TO RESPONSE DTO
    private BookingResponse toResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getService().getName(),
                booking.getService().getCategory(),
                booking.getPrice(),
                booking.getCustomerName(),
                booking.getCustomerEmail(),
                booking.getCustomerPhone(),
                booking.getServiceAddress(),
                booking.getBookingDate(),
                booking.getPreferredTime(),
                booking.getDescription(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
    // GET ACTIVE SERVICES FOR CUSTOMER BOOKING
@Transactional(readOnly = true)
public List<AvailableServiceResponse> getAvailableServices() {

    return workerServiceRepository
            .findByActiveTrueOrderByNameAsc()
            .stream()
            .map(service -> new AvailableServiceResponse(
                    service.getId(),
                    service.getName(),
                    service.getCategory(),
                    service.getDescription(),
                    service.getPrice(),
                    service.getLocation()
            ))
            .toList();
}
}
