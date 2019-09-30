package ru.ryabtsev.cloud.client.commands;

/**
 * Base class for commands which can be used like a part of command sequence.
 */
public abstract class SequentialCommand implements Command {

    private Command next = null;

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Command next() {
        return next;
    }

    @Override
    public void add(Command command) {
        next = command;
    }
}
