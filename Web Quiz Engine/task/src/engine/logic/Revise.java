package engine.logic;

import engine.domain.Quiz;

import java.util.List;

public class Revise {

    private Quiz quiz;

    public Revise(Quiz quiz) {
        this.quiz = quiz;
    }

    public ResponseQuiz getResponseQuiz(List<Integer> answer) {
        if (quiz.getAnswer().toString().equals(answer.toString())) {
            return new ResponseQuiz(true);
        } else {
            return new ResponseQuiz(false);
        }
    }
}