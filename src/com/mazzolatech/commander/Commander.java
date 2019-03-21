package com.mazzolatech.commander;

import core.Debugger;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Commander implements Runnable {

    protected static Debugger debugger;

    private static ArrayList<Command> commands = new ArrayList<>();
    private Scanner reader = new Scanner(System.in);

    private static boolean running = true;

    /**
     * Initializes the commander and adds all commands that are executable.
     */
    public Commander(String logFile) {
        debugger = new Debugger("Commander", "1.0.0", logFile);
        Command.initializeDebugger(logFile);
        // Adds the initial help command
        this.addCommand(new Help().addAltName("list").addAltName("show"));
    }

    /**
     * Adds and initializes a new command to the Commander.
     * @param command - The command to be initialized.
     */
    public static void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Compares the passed command through the list of available commands (including alternate names).
     * @param command - The name of the command you would like to run.
     */
    public static void checkCommand(String command) {
        debugger.log("Command \"" + command + "\" entered.");
        String commandName = command.split(" ")[0];
        ArrayList<String> arguments = new ArrayList<>();

        for (String arg: command.split(" ")) {
            if (!arg.equalsIgnoreCase(commandName)) {
                arguments.add(arg);
            }
        }

        for (Command com: commands) {
            if (com.name.equalsIgnoreCase(commandName)) {
                com.action(arguments);
                return;
            }
            if (com.altNames != null) {
                for (String name: com.altNames) {
                    if (name.equalsIgnoreCase(commandName)) {
                        com.action(arguments);
                        return;
                    }
                }
            }
        }
        debugger.print("Command \"" + command + "\" not found. Type \"help\" for a list of available commands.");
        debugger.alert("Command \"" + command + "\" not found.");
    }

    /**
     * Compares the passed command through the list of available commands (including alternate names).
     * @param command - The name of the command you would like to run.
     */
    public static void checkCommand(String command, DataOutputStream out) {
        debugger.log("Command \"" + command + "\" entered with network connections.");
        String commandName = command.split(" ")[0];
        ArrayList<String> arguments = new ArrayList<>();

        for (String arg: command.split(" ")) {
            if (!arg.equalsIgnoreCase(commandName)) {
                arguments.add(arg);
            }
        }

        for (Command com: commands) {
            if (com.name.equalsIgnoreCase(commandName)) {
                com.action(arguments, out);
                return;
            }
            if (com.altNames != null) {
                for (String name: com.altNames) {
                    if (name.equalsIgnoreCase(commandName)) {
                        com.action(arguments, out);
                        return;
                    }
                }
            }
        }
        debugger.print("Command \"" + command + "\" not found. Type \"help\" for a list of available commands.");
        debugger.alert("Command \"" + command + "\" not found.");
    }

    /**
     * Spins up the Commander thread and begins accepting input.
     */
    @Override
    public void run() {
        debugger.log("Beginning Commander Loop");
        while(running) {
            checkCommand(reader.nextLine());
        }
        reader.close();
    }

    public static ArrayList<Command> getCommandsList() {
        return commands;
    }

    public static void main(String args[]) {}

}

/**
 * Used to list all available commands in the command list. As well as available arguments.
 */
class Help extends Command {
    public Help() {
        super("help", "Lists information about a specific command, or pass \"all\" to list all commands & parameters.");
        this.arguments.add(new All());
    }

    @Override
    public void action(ArrayList<String> arguments) {
        debugger.log("Help command called.");
        String[] args = {};
        args = arguments.toArray(args);
        // TODO: Add pages to the help list, similar to Minecraft forge.
        if (args.length < 1) {
            debugger.print("======= HELP LIST =======");
            debugger.print("The following commands are currently available.");
            debugger.print("Pass the command that you would like to learn more about, or \"all\" to see all available commands & parameters.");
            for (Command com: Commander.getCommandsList()) {
                System.out.println(" - " + com.name +
                        (com.altNames.size()>0 ? " " + com.altNames.toString() : "") + ": " + com.description);
            }
        } else {
            checkArguments(arguments);
            for (Command com: Commander.getCommandsList()) {
                if (com.name.equalsIgnoreCase(args[0])) {
                    System.out.println(com.description);
                }
            }
        }
    }

    class All extends Command.Argument {
        public All() {
            super("all", "Displays all currently accessible commands & parameters.");
        }

        @Override
        public void action(String[] args) {
            debugger.print("======= HELP LIST =======");
            System.out.println("The following commands are currently active.");
            for (Command com: Commander.getCommandsList()) {
                if (com.isActive) {
                    System.out.println(" - " + com.name +
                            (com.altNames.size()>0 ? " " + com.altNames.toString() : "") + ": " + com.description);
                    for (Argument arg : com.arguments) {
                        System.out.println(" | - " + arg.name + ": " + arg.description);
                    }
                }
            }
        }
    }
}
