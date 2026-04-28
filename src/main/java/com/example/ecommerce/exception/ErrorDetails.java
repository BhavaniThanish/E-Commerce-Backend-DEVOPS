package com.example.ecommerce.exception;
import java.time.LocalDateTime;
public class ErrorDetails {
 private LocalDateTime timestamp;
 private String message;
 private String details;
 public ErrorDetails(LocalDateTime timestamp, String message, string details) {
  this.timestamp = timestamp;
  this.message = message;
  this.details = details;
}
public LocalDateTime getTimestamp() { return timestamp;}
public String getMessage() { return message;}
public String getDetails() { return details; }
}
