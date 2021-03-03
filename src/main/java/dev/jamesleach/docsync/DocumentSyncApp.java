package dev.jamesleach.docsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point.
 */
@SpringBootApplication
@EnableScheduling
public class DocumentSyncApp {
  public static void main(String[] args) {
    SpringApplication.run(DocumentSyncApp.class, args).start();
  }
}
