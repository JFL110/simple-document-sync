package dev.jamesleach.docsync;

import lombok.Data;

import java.util.List;

@Data
class Classroom {
  private final String id;
  private final List<Document> documents;
}
