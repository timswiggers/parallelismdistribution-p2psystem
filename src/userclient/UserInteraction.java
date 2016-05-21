package userclient;

/**
 * Created by timsw on 07/05/2016.
 */
public interface UserInteraction {
    void newLine();
    void say(String message);
    void sayList(String... messages);
    void sayError(Exception error);
    String prompt(String prompt);
    String askForValue(String question);
    String askForValue(String question, String defaultAnswer);
    boolean askYesNoQuestion(String question);
}
