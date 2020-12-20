package engine.domain;

import engine.domain.Customer;
import engine.domain.Quiz;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CompleteQuizzes {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private LocalDateTime date;
    @ManyToMany
    private List<Quiz> quizList = new ArrayList<>();
    @ManyToMany List<Customer> customerList = new ArrayList<>();

    public CompleteQuizzes() {
    }

    public void addQuiz(Quiz quiz) {
        quizList.add(quiz);
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    public void addCustomer(Customer customer) {
        customerList.add(customer);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }
}
