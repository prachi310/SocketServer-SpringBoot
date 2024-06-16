package com.example.ClientSocketService.controllers;

import com.example.ClientSocketService.dto.*;
import lombok.Synchronized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final RestTemplate restTemplate;

    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.restTemplate=new RestTemplate();
    }

    @PostMapping("/newRide")
    public ResponseEntity<Boolean> raiseRideRequest(@RequestBody RideRequestDto requestDto){
        sendDriverNeqRideRequest(requestDto);
        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.OK);
    }


    public void sendDriverNeqRideRequest(RideRequestDto requestDto) {
        System.out.println("Executed periodic function");
        //TODO: ideally request shd go to only near by driver but for simplicity we are sending it to all
        simpMessagingTemplate.convertAndSend("/topic/rideRequest",requestDto);
    }

    @MessageMapping("/rideResponse/{userId}")
    public synchronized void rideResponseHandler(@DestinationVariable String userId, RideResponseDto rideResponseDto) {
        System.out.println(rideResponseDto.getResponse()+ " " + userId);
        UpdateBookingRequestDto updateBookingRequestDto=UpdateBookingRequestDto.builder()
                .driverId(Optional.of(Long.valueOf(userId)))
                .status("SCHEDULED")
                .build();
        ResponseEntity<UpdateBookingResponseDto> result= this.restTemplate.postForEntity("http://localhost:8000/api/v1/booking/"
                        +rideResponseDto.bookingId,updateBookingRequestDto,UpdateBookingResponseDto.class);

        System.out.println(result.getStatusCode());
    }

}
