package com.example.ClientSocketService.controllers;

import com.example.ClientSocketService.dto.RideRequestDto;
import com.example.ClientSocketService.dto.RideResponseDto;
import com.example.ClientSocketService.dto.TestRequest;
import com.example.ClientSocketService.dto.TestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
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

    @MessageMapping("/rideResponse")
    public void rideResponseHandler(RideResponseDto rideResponseDto) {
        System.out.println(rideResponseDto.getResponse());
    }

}
