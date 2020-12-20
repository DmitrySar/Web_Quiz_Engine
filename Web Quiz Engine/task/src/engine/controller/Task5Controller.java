package engine.controller;

import engine.domain.*;
import engine.logic.ResponseQuiz;
import engine.logic.Revise;
import engine.repository.CompleteQuizzesRepository;
import engine.repository.CustomerRepository;
import engine.repository.QuizRepository;
import engine.service.CurrentCustomer;
import engine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Task5Controller {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    QuizService quizService;
    @Autowired
    private CurrentCustomer currentCustomer;
    @Autowired
    private CompleteQuizzesRepository completeQuizzesRepository;

    @PostMapping("/api/quizzes")
    public Quiz addQuiz(@RequestBody @Valid Quiz quiz) {
        Customer customer = currentCustomer.get();
        quiz.setCustomer(customer);
        quizRepository.save(quiz);
        customer.addQuiz(quiz);
        customerRepository.save(customer);
        return quiz;
    }

    @GetMapping("/api/quizzes/{id}")
    public Quiz findById(@PathVariable int id) {
        Quiz quiz;
        try {
            quiz = quizRepository.findById(id).get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return quiz;
    }

    @GetMapping("/api/quizzes")
    public ResponseEntity<Page<Quiz>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy));
        return new ResponseEntity<Page<Quiz>>(quizRepository.findAll(paging), new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/api/quizzes/{id}/solve")
    public ResponseQuiz responseQuiz(@PathVariable int id, @RequestBody Answer answer) {
        Quiz quiz = quizRepository.findById(id).get();
        Revise revise = new Revise(quiz);
        if (revise.getResponseQuiz(answer.getAnswer()).isSuccess()) {
            CompleteQuizzes completeQuizzes = new CompleteQuizzes();
            completeQuizzes.setDate(LocalDateTime.now());
            completeQuizzes.addQuiz(quiz);
            completeQuizzes.addCustomer(currentCustomer.get());
            completeQuizzesRepository.save(completeQuizzes);
        }
        return revise.getResponseQuiz(answer.getAnswer());
    }

    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<Page<CompleteQuiz>> completeQuizzes(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize

    ) {
        Sort sortOrder = Sort.by("date");
        Pageable pageable = PageRequest.of(page, pageSize);
        List<CompleteQuizzes> completeQuizzes = completeQuizzesRepository
                .findCompleteQuizzesByCustomerListContains(currentCustomer.get(), sortOrder.descending());
        List<CompleteQuiz> res = completeQuizzes.stream()
                .map(c -> new CompleteQuiz(c.getQuizList().get(0).getId(), c.getDate().toString()))
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = (int) ((start + pageable.getPageSize()) > res.size() ? res.size()
                : (start + pageable.getPageSize()));
        Page<CompleteQuiz> completeQuizPage = new PageImpl<CompleteQuiz>(res.subList(start, end), pageable, res.size());

        return new ResponseEntity<Page<CompleteQuiz>>(completeQuizPage, new HttpHeaders(), HttpStatus.OK);
    }


}