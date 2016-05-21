package userclient.console;

import userclient.UserInteraction;

import java.io.*;

/**
 * Created by timsw on 08/05/2016.
 */
public class ConsoleUserInteraction implements UserInteraction {

    private final BufferedReader in;
    private final PrintStream out;

    public ConsoleUserInteraction(InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }

    @Override
    public void newLine() {
        out.println();
    }

    @Override
    public void say(String message) {
        out.println(message);
    }

    @Override
    public void sayList(String... messages) {
        for(String message : messages){
            say("- " + message);
        }
    }

    @Override
    public String prompt(String prompt) {
        out.printf("%s>", prompt);
        String value = safeReadLine();
        return value != null ? value : "";
    }

    @Override
    public void sayError(Exception error) {
        out.println("ERROR! - " + drillDownMessage(error));
        if(error != null) error.printStackTrace();
    }

    @Override
    public String askForValue(String question) {
        out.printf("Please provide the %s:", question);
        return safeReadLine();
    }

    @Override
    public String askForValue(String question, String defaultAnswer) {
        out.printf("Please provide the %s (%s):", question, defaultAnswer);
        String answer = safeReadLine();
        return (answer != null && !answer.isEmpty()) ? answer : defaultAnswer;
    }

    @Override
    public boolean askYesNoQuestion(String question) {
        out.printf("%s (Y/n):", question);
        String answer = safeReadLine();
        return toYesNo(answer, true);
    }

    private String safeReadLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            sayError(e);
            return null;
        }
    }

    private boolean toYesNo(String answer, boolean defaultValue){
        if(answer ==  null || answer.isEmpty()) {
            return defaultValue;
        }
        return answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes");
    }

    private String drillDownMessage(Throwable e){
        if(e == null) return "";

        Throwable cause;

        while((cause = e.getCause()) != null) {
            e = e.getCause();
        }
        return e.getMessage();
    }
}
