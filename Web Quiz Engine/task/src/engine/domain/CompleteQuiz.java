package engine.domain;

public class CompleteQuiz {
    private int id;
    private String completedAt;

    public CompleteQuiz(int id, String completedAt) {
        this.id = id;
        this.completedAt = completedAt;
    }

    public int getId() {
        return id;
    }

    public String getCompletedAt() {
        return completedAt;
    }
}
