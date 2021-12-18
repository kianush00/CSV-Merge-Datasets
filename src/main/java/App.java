import view.CommandLineProcess;

public class App {

    public static void main(String[] args) {
        /*
         * This is the App launcher which only calls the rest of your logic.
         * For example you can call view/CommandLineProcess.java
         */
        CommandLineProcess clp = new CommandLineProcess();
        clp.processParameters(args);
    }
}
