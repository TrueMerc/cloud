package ru.ryabtsev.cloud.client.commands;

/**
 * Provides interface for client side commands.
 */
public interface Command {

    /**
     * Executes command.
     */
    void execute();

    /**
     * Returns true if the next command is present and false in the other case.
     * @return true if the next command is present and false in the other case.
     */
    boolean hasNext();

    /**
     * Returns next command in the command chain if it presents or null it the other case.
     * @return next command in the command chain if it presents or null it the other case.
     */
    Command next();

    /**
     * Adds next command to a command sequence.
     */
    void add(Command command);
}
