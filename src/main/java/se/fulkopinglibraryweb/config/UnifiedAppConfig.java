package se.fulkopinglibraryweb.config;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.*;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import se.fulkopinglibraryweb.utils.ValidationUtils;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.service.AsyncBookServiceImpl;
import se.fulkopinglibraryweb.service.impl.LoanServiceImpl;
import se.fulkopinglibraryweb.repository.UserRepository;
import se.fulkopinglibraryweb.utils.PasswordUtils;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.service.interfaces.LibraryService;
import se.fulkopinglibraryweb.service.impl.LibraryServiceImpl;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.service.interfaces.MagazineService;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.service.impl.UserServiceImpl;
import se.fulkopinglibraryweb.service.impl.MagazineServiceImpl;
import se.fulkopinglibraryweb.service.impl.MediaServiceImpl;
import se.fulkopinglibraryweb.service.impl.FirestoreSearchService;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.model.Media;

import java.util.concurrent.ExecutionException;

@Configuration
@ComponentScan(basePackages = "se.fulkopinglibraryweb")
@EnableAspectJAutoProxy
@Profile({"dev", "prod", "test"})
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class UnifiedAppConfig {

    // Validation beans
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
        return validatorFactory;
    }

    @Bean
    public ValidationUtils validationUtils() {
        return new ValidationUtils();
    }

    // Firestore collection references
    @Bean
    public CollectionReference magazineCollection(Firestore firestore) {
        return firestore.collection("magazines");
    }

    @Bean
    public CollectionReference mediaCollection(Firestore firestore) {
        return firestore.collection("media");
    }

    @Bean
    public CollectionReference loanCollection(Firestore firestore) {
        return firestore.collection("loans");
    }

    @Bean
    public CollectionReference bookCollection(Firestore firestore) {
        return firestore.collection("books");
    }

    // Service layer beans
    @Bean
    public BookService bookService(BookRepository bookRepository) {
        return new AsyncBookServiceImpl(bookRepository);
    }

    @Bean 
    public UserService userService(Firestore firestore, UserRepository userRepository, PasswordUtils passwordUtils) {
        return new UserServiceImpl(firestore, userRepository, passwordUtils);
    }

    @Bean
    public LibraryService libraryService(BookService bookService, se.fulkopinglibraryweb.service.interfaces.UserService userService, LoanService loanService) {
        return new LibraryServiceImpl(bookService, userService, loanService);
    }

    @Bean
    public MagazineService magazineService(MagazineRepository magazineRepository) {
        return new MagazineServiceImpl(magazineRepository);
    }

    @Bean 
    public LoanService loanService(Firestore firestore, 
                                 BookRepository bookRepository,
                                 MagazineRepository magazineRepository,
                                 MediaRepository mediaRepository) {
        return new LoanServiceImpl(firestore, bookRepository, magazineRepository, mediaRepository);
    }

    @Bean
    public MediaService mediaService(MediaRepository mediaRepository, LoanService loanService) {
        return new MediaServiceImpl(mediaRepository, loanService);
    }

    // Search service beans with proper configuration profiles
    @Bean
    @Profile({"dev", "prod"})
    public FirestoreSearchService<Book> bookSearchService(CollectionReference bookCollection) {
        return new FirestoreSearchService<>(bookCollection, Book.class);
    }

    @Bean
    @Profile({"dev", "prod"})
    public FirestoreSearchService<Magazine> magazineSearchService(CollectionReference magazineCollection) {
        return new FirestoreSearchService<>(magazineCollection, Magazine.class);
    }

    @Bean
    @Profile({"dev", "prod"})
    public FirestoreSearchService<Media> mediaSearchService(CollectionReference mediaCollection) {
        return new FirestoreSearchService<>(mediaCollection, Media.class);
    }
}
