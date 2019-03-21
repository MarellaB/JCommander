package com.mazzolatech.commander;

import core.Debugger;

import java.io.DataOutputStream;
import java.util.ArrayList;

public class Command {

    protected static Debugger debugger;

    public String name;
    public ArrayList<String> altNames = new ArrayList<>();
    public String description;
    public ArrayList<Argument> arguments = new ArrayList<>();

    public boolean isActive = true;

    /**
     * The initial command object.
     * @param name - The called name of the command.
     * @param description - What the command will be doing, will print to the console when command is listed.
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        debugger.log("Command [" + name + "] added.");
    }

    public static void initializeDebugger(String logFile) {
        debugger = new Debugger("Command-List", "1.0.0", logFile);
    }

    /**
     * The action to be run when the command is called
     */
    public void action(ArrayList<String> arguments) {}

    // TODO: Help will currently not check for alternate names.
    protected void checkArguments(ArrayList<String> passedArguments) {
        String Arguments[] = {};
        Arguments = passedArguments.toArray(Arguments);

        for (Argument arg : this.arguments) {
            if (arg.name.equalsIgnoreCase(Arguments[0])) {
                arg.action(Arguments);
            }
        }
    }

    protected void checkArguments(ArrayList<String> passedArguments, DataOutputStream data) {
        String Arguments[] = {};
        Arguments = passedArguments.toArray(Arguments);

        for (Argument arg : this.arguments) {
            if (arg.name.equalsIgnoreCase(Arguments[0])) {
                arg.action(Arguments, data);
            }
        }
    }

    /**
     * The action to be run when the command is called, with network out access
     */
    public void action(ArrayList<String> arguments, DataOutputStream out) {}

    /**
     * Adds an alternate name for the command to be called by. Will print to the console when command is listed.
     * @param altName - The alternate name for the command
     * @return - Returns the command object to allow chaining in declaration.
     */
    public Command addAltName(String altName) {
        this.altNames.add(altName);
        return this;
    }

    public class Argument {
        public String name;
        public String description = "No Description Given";
        public Argument(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void action(String[] parameters) {

        }

        public void action(String[] parameters, DataOutputStream out) {

        }
    }
}

