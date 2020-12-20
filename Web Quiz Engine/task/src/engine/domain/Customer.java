package engine.domain;

import engine.domain.Quiz;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private int id;
    @Email(regexp = ".+@.+\\..+")
    private String email;
    @Size(min = 5)
    private String password;

    @OneToMany
    private List<Quiz> quizzes = new ArrayList<>();

    public Customer() {
    }

    public Customer(@Email String email, @Size(min = 5) String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }
}
