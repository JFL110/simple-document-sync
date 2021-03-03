package dev.jamesleach.docsync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Controller for all document websocket messages.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
class DocumentWebsocketController {
  private final DocumentService documentService;

  @MessageMapping("classroom/{classroomId}/document/{documentId}/update")
  @SendTo("/topic/classroom/{classroomId}/all-documents")
  Document updateDocument(@DestinationVariable("classroomId") String classroomId,
                          @DestinationVariable("documentId") String documentId,
                          String content) {
    if (DocumentService.MASTER_DOCUMENT_ID.equals(documentId)) {
      throw new IllegalArgumentException("Cannot use updateDocument to update the master document.");
    }

    log.debug("Update {} {} with '{}'", classroomId, documentId, content);
    return documentService.updateDocument(classroomId, documentId, content).orElse(null);
  }

  @MessageMapping("classroom/{classroomId}/update-master")
  @SendTo("/topic/classroom/{classroomId}/master")
  Document updateMasterDocument(@DestinationVariable("classroomId") String classroomId,
                                String content) {
    log.debug("Update master {} with {}", classroomId, content);
    return documentService.updateDocument(classroomId, DocumentService.MASTER_DOCUMENT_ID, content).orElse(null);
  }
}
