package engine.logic;

public class ResponseQuiz {

    public static final String SUCCESS = "Congratulations, you're right!";
    public static final String NOT_SUCCESS = "Wrong answer! Please, try again.";
    private boolean success;

    public ResponseQuiz(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFeedback() {
        return success ? SUCCESS : NOT_SUCCESS;
    }
}