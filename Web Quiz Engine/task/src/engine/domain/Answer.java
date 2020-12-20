package engine.domain;

import java.util.List;

public class Answer {
    private List<Integer> answer;

    public Answer() {
    }

    public Answer(List<Integer> answer) {
        this.answer = answer;
    }

    public List<Integer> getAnswer() {
        return answer;
    }
}