package dev.jamesleach.docsync;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration for all websockets.
 *
 * @author jim
 */
@Configuration
@EnableWebSocketMessageBroker
class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    var handshakeHandler = new DefaultHandshakeHandler() {
      @Override
      protected Principal determineUser(@NonNull ServerHttpRequest request,
                                        @NonNull WebSocketHandler wsHandler,
                                        @NonNull Map<String, Object> attributes) {
        return new SimpleUser(UUID.randomUUID().toString());
      }
    };

    registry.addEndpoint("/classroom")
      .setAllowedOriginPatterns("http://*", "https://*", "file://*", "null")
      .setHandshakeHandler(handshakeHandler);
    registry.addEndpoint("/classroom")
      .setAllowedOriginPatterns("http://*", "https://*", "file://*", "null")
      .setHandshakeHandler(handshakeHandler)
      .withSockJS();
  }

  /**
   * Simple implementation of Principal with ID.
   */
  @Data
  static class SimpleUser implements Principal {
    private final String id;

    @Override
    public String getName() {
      return id;
    }
  }
}