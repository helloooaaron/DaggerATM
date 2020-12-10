package com.example.daggeratm;

import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Component;

public final class CommandRouter {
    private static final String LOG_TAG = CommandRouter.class.getSimpleName();
    private final Map<String, Command> commands;

/* Commented out for enabling multiple commands */
//    @Inject // Tell Dagger to call (and how) this constructor when we ask for a CommandRouter
//    public CommandRouter(Command command /* Deps to CommandRouter class */) {
//        commands = new HashMap<String, Command>();
//        commands.put(command.key(), command);
//    }

    @Inject
    public CommandRouter(Map<String, Command> commands /* Multibinding into Map */) {
        // This map contains:
        // "hello" -> HelloWorldCommand
        // "login" -> LoginCommand
        this.commands = commands;
    }

    Command.Status route(String input, TextView view) {
        List<String> splitInput = split(input);
        if (splitInput.isEmpty()) return invalidCommand(input);

        String commandKey = splitInput.get(0);
        Command command = commands.get(commandKey);
        if (command == null) return invalidCommand(input);

        Command.Status status = command.handleInput(splitInput.subList(1, splitInput.size()));
        if (status == Command.Status.INVALID) {
            Log.e(LOG_TAG, commandKey + ": invalid arguments");
        }
        return status;
    }

    private Command.Status invalidCommand(String input) {
        Log.e(LOG_TAG, String.format("Couldn't understand '%s'. Please try again.", input));
        return Command.Status.INVALID;
    }

    private static List<String> split(String string) {
        return Arrays.asList(string.split(" "));
    }
}

/**
 * Dagger will generate an implementation DaggerCommandRouterFactory of this interface.
 * Dagger will create a static create() method to give us an instance of the class
 */
@Component(modules = {
        HelloWorldModule.class /* Tell Dagger to look for @Binds method in HelloWorldModule */,
        LoginCommandModule.class, /* Tell Dagger to use LoginModule as instructions */
        LoggingOutModule.class /* Tell Dagger to use LoggingOutModule as instructions */
})
interface CommandRouterFactory {
    /**
     * Entry point method.
     */
    CommandRouter router();
}
