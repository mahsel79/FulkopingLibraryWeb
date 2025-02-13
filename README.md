# **Fulköping Library System**

The Fulköping E-Library WEB System is a **Java web application** designed to help the staff and residents of Fulköping manage and explore the library's collection of books, magazines, and media. The system allows users to search for items, borrow and return books, view loan history, and update their profiles.

---

Here’s the updated **Technologies Used** section reflecting our migration to Firestore and JSP/Servlets:  

---

## **Technologies Used**  
- **Programming Language**: Java 21  
- **Web Framework**: JSP & Servlets (Jakarta EE)  
- **Database**: Firestore (Google Cloud)  
- **Database Management**: Firestore SDK  
- **Secret Management**: Google Secrets Manager  
- **Web Server**: Apache Tomcat  
- **Build Tool**: Maven  
- **IDE**: IntelliJ IDEA (recommended)  
- **Libraries**:  
  - Firestore Java SDK (for database connectivity)  
  - Google Cloud Client Library (for Firestore and Secrets Manager)  
  - SLF4J (for logging)  


---

## **Folder and File Structure**
```
src/
│── main/
│   │── java/com/fulkoping/library/
│   │   │── controllers/
│   │   │   ├── BookController.java
│   │   │   ├── MagazineController.java
│   │   │   ├── MediaController.java
│   │   │   ├── UserController.java
│   │   │
│   │   │── models/
│   │   │   ├── Book.java
│   │   │   ├── BookItem.java
│   │   │   ├── ItemType.java
│   │   │   ├── LibraryItem.java
│   │   │   ├── Magazine.java
│   │   │   ├── MagazineItem.java
│   │   │   ├── Media.java
│   │   │   ├── MediaItem.java
│   │   │   ├── MediaType.java
│   │   │   ├── MediaTypeImpl.java
│   │   │   ├── User.java
│   │   │
│   │   │── services/
│   │   │   ├── BookService.java
│   │   │   ├── MagazineService.java
│   │   │   ├── MediaService.java
│   │   │   ├── SearchBook.java
│   │   │   ├── UserService.java
│   │   │
│   │   │── utils/
│   │   │   ├── FirestoreUtil.java  <-- To handle Firestore connection
│   │   │   ├── GoogleSecretsUtil.java  <-- To fetch secrets from Google Secrets Manager
│   │   │   ├── LibraryApp.java
│   │   │
│   │   │── servlets/
│   │   │   ├── BookServlet.java
│   │   │   ├── MagazineServlet.java
│   │   │   ├── MediaServlet.java
│   │   │   ├── UserServlet.java
│
│── webapp/  <-- JSP Files
│   ├── index.jsp
│   ├── login.jsp
│   ├── dashboard.jsp
│   ├── books.jsp
│   ├── magazines.jsp
│   ├── media.jsp
│
│── resources/
│   ├── web.xml  <-- Web deployment descriptor (Servlet configuration)
│
│── test/  <-- Unit tests
|
│── README.md
```

---
