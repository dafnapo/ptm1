package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

    ArrayList<Command> commands;
    DefaultIO dio;
    Commands c;

    public CLI(DefaultIO dio) {
        this.dio = dio;
        c = new Commands(dio);
        commands = new ArrayList<>();
        // implement
        commands.add(c.new ExampleCommand());
        commands.add(c.new uploadTimeSeries());
        commands.add(c.new algoSettings());
        commands.add(c.new detectAnomalies());
        commands.add(c.new displayResults());
        commands.add(c.new uploadAnomaliesAnalyzeResults());
    }

    public void start() {
        int option = 0;
        while (true) {
            dio.write("Welcome to the Anomaly Detection Server.\n" +
                    "Please choose an option:\n");
            for (int i = 1; i <= 5; i++) {
                dio.write(commands.get(i).description);
            }
            dio.write("6. exit\n");
            option = (int) dio.readVal();
            if (option == 6) {
                break;
            }
            commands.get(option).execute();
        }
    }
}