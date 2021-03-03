package dev.jamesleach.docsync;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Save/Load classrooms to disk.
 */
@Component
@Slf4j
class LocalFileClassroomStorage {
  private static final Path STORAGE_DIR = Paths.get("./classroom-data/");
  private static final String FILE_NAME_SUFFIX = ".classroom.json";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @SneakyThrows
  List<Classroom> loadAll() {
    log.info("Loading all classrooms");
    initDir();
    return Files.walk(STORAGE_DIR)
      .filter(f -> f.toString().endsWith(FILE_NAME_SUFFIX))
      .map(f -> {
        try {
          return objectMapper.readValue(f.toFile(), Classroom.class);
        } catch (IOException e) {
          log.error("Error reading classroom " + f.getFileName(), e);
          return null;
        }
      }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @SneakyThrows
  void saveAll(List<Classroom> classroom) {
    if (classroom.isEmpty()) {
      return;
    }
    log.info("Saving {} classroom/s", classroom.size());
    initDir();
    classroom.forEach(c -> {
      try {
        Files.write(
          STORAGE_DIR.resolve(c.getId() + FILE_NAME_SUFFIX),
          objectMapper.writeValueAsBytes(c));
      } catch (IOException e) {
        throw new RuntimeException("Error writing classroom", e);
      }
    });
  }

  private void initDir() {
    if (!STORAGE_DIR.toFile().exists()) {
      boolean unused = STORAGE_DIR.toFile().mkdir();
    }
  }
}
