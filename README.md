# Simple Document Sync
[![Java CI](https://github.com/JFL110/simple-document-sync/actions/workflows/build.yml/badge.svg)](https://github.com/JFL110/simple-document-sync/actions/workflows/build.yml)

### Operation
Java app that uses websockets to synchronize text 'documents' with clients. Documents are grouped in 'classrooms', each having a 'master' document. Student clients can view the master document and write their own documents, teacher clients can view all student documents and edit the master document. Classrooms are saved to disk periodically and all saved classrooms are loaded from disk on application start-up.

### Running locally
- Clone the repo
- In the repo run ```./gradlew bootRun```
- In two browser tabs (side by side) navgiate to http://localhost:8080/demo-ui-student.html and http://localhost:8080/demo-ui-teacher.html
