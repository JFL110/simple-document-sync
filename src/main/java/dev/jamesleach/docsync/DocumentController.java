package dev.jamesleach.docsync;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Controller for all document REST messages.
 */
@RestController
@RequiredArgsConstructor
class DocumentController {
  private final DocumentService documentService;

  @GetMapping("/classroom/{classroomId}/document/{documentId}")
  Document getDocument(@PathVariable("classroomId") String classroomId,
                       @PathVariable("documentId") String documentId) {
    return documentService.get(classroomId, documentId).orElse(null);
  }

  @GetMapping("/classroom/{classroomId}/all-documents")
  Collection<Document> getAllDocument(@PathVariable("classroomId") String classroomId) {
    return documentService.getAll(classroomId);
  }

  @GetMapping("/classroom/{classroomId}/master-document")
  Document getMasterDocument(@PathVariable("classroomId") String classroomId) {
    return documentService.get(classroomId, DocumentService.MASTER_DOCUMENT_ID).orElse(null);
  }
}
