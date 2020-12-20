package engine.controller;

import engine.domain.CompleteQuizzes;
import engine.domain.Customer;
import engine.domain.Quiz;
import engine.repository.CompleteQuizzesRepository;
import engine.repository.CustomerRepository;
import engine.repository.QuizRepository;
import engine.service.CurrentCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SecurityController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CurrentCustomer currentCustomer;
    @Autowired
    private CompleteQuizzesRepository completeQuizzesRepository;

    @PostMapping("/api/register")
    public String addCustomer(@RequestBody @Valid Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        customer = new Customer(customer.getEmail(), passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        return "ok";
    }

    @DeleteMapping("/api/quizzes/{id}")
    public ResponseEntity delQuiz(@PathVariable int id) {
        Customer customer = currentCustomer.get();
        Optional<Quiz> quizOptional = quizRepository.findById(id);
        Quiz quiz;

        if (quizOptional.isPresent()) {
            quiz = quizOptional.get();
            if (quiz.getCustomer().equals(customer)) {
                
                completeQuizzesRepository.findAll().stream().forEach(c -> c.setQuizList(c.getQuizList().stream()
                        .filter(q -> q.getId() != id).collect(Collectors.toList())));

                customer.setQuizzes(customer.getQuizzes().stream().filter(q -> q.getId() != id).collect(Collectors.toList()));
                customerRepository.save(customer);
                quizRepository.deleteById(id);

                return new ResponseEntity(HttpStatus.NO_CONTENT);//"204";
            } else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);//"403";
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);//"404";
    }

}
