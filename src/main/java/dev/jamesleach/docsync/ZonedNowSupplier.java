package dev.jamesleach.docsync;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

/**
 * Inject the time - to make mocking easy.
 */
@Component
class ZonedNowSupplier implements Supplier<ZonedDateTime> {
  @Override
  public ZonedDateTime get() {
    return ZonedDateTime.now();
  }
}
