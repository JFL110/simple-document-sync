package dev.jamesleach.docsync;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
class Document {
  private final long revisionTime;
  private final String id;
  private final String content;
}
