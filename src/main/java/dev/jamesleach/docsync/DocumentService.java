package dev.jamesleach.docsync;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Document management logic.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class DocumentService {
  static final String MASTER_DOCUMENT_ID = "$master$";
  private static final long SAVE_EVERY_MS = 10000;

  private final Map<String, MutableClassroom> classrooms = Maps.newConcurrentMap();
  private final Lock writeLock = new ReentrantLock();
  private final ZonedNowSupplier nowSupplier;
  private final LocalFileClassroomStorage classroomStorage;

  @PostConstruct
  void initialLoad() {
    writeLock.lock();
    try {
      classroomStorage.loadAll()
        .forEach(c -> {
          log.info("Loading classroom {}", c.getId());
          var classroom = classrooms.computeIfAbsent(c.getId(), x -> new MutableClassroom());
          c.getDocuments().forEach(d -> classroom.getDocuments().put(d.getId(), d));
        });
    } finally {
      writeLock.unlock();
    }
  }

  @Scheduled(fixedDelay = SAVE_EVERY_MS)
  void saveClassrooms() {
    var classroomsToSave = classrooms.entrySet()
      .stream()
      .filter(e -> e.getValue().getCurrentRevision().get() != e.getValue().getLastSavedAtRevision().get())
      .map(e -> {
        writeLock.lock();
        try {
          e.getValue().getLastSavedAtRevision().set(e.getValue().getCurrentRevision().get());
          return new Classroom(
            e.getKey(),
            ImmutableList.copyOf(e.getValue().getDocuments().values())
          );
        } finally {
          writeLock.unlock();
        }
      })
      .collect(Collectors.toList());

    classroomStorage.saveAll(classroomsToSave);
  }

  Optional<Document> get(String classroomId, String documentId) {
    return Optional
      .ofNullable(classrooms.get(classroomId))
      .flatMap(c -> Optional.ofNullable(c.getDocuments().get(documentId)));
  }

  Collection<Document> getAll(String classroomId) {
    return Optional
      .ofNullable(classrooms.get(classroomId))
      .map(c -> c.getDocuments().values())
      .orElse(ImmutableList.of());
  }

  Optional<Document> updateDocument(String classroomId, String documentId, String documentContents) {
    long revisionTime = nowSupplier.get().toInstant().toEpochMilli();
    writeLock.lock();
    try {
      var classroom = classrooms.computeIfAbsent(classroomId, x -> new MutableClassroom());
      var document = classroom.getDocuments().get(documentId);

      // Ignore if no change or this request is older
      if (document != null && (
        document.getRevisionTime() > revisionTime || Objects.equals(document.getContent(), documentContents))) {
        return Optional.empty();
      }

      var newDocument = new Document(revisionTime, documentId, documentContents);
      classroom.getDocuments().put(documentId, newDocument);
      classroom.getCurrentRevision().getAndUpdate(current -> Math.max(revisionTime, current));
      return Optional.of(newDocument);
    } finally {
      writeLock.unlock();
    }
  }

  @Data
  static class MutableClassroom {
    private final AtomicLong lastSavedAtRevision = new AtomicLong();
    private final AtomicLong currentRevision = new AtomicLong();
    private final Map<String, Document> documents = Maps.newConcurrentMap();
  }
}
