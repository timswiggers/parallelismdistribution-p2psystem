package userclient;

public interface UserInteraction {
    void newLine();
    void say(String message);
    void sayPartly(String message);
    void sayList(String... messages);
    void sayError(String errorMessage);
    void sayError(Exception error);
    String prompt(String prompt);
    String ask(String question);
    String askForValue(String question, String defaultAnswer);
    boolean askYesNoQuestion(String question);
}
