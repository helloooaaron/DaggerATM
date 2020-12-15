package com.example.daggeratm;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import com.example.daggeratm.Command.Status;
import com.example.daggeratm.Command.Result;

public final class CommandRouter {
    private static final String LOG_TAG = CommandRouter.class.getSimpleName();
    private final Map<String, Command> commands;

/* Commented out for enabling multiple commands */
//    @Inject // Tell Dagger to call (and how) this constructor when we ask for a CommandRouter
//    public CommandRouter(Command command /* Deps to CommandRouter class */) {
//        commands = new HashMap<String, Command>();
//        commands.put(command.key(), command);
//    }

    @Inject // Inject dependency on XXXCommandModule(s) declared in @Component whichever can provide Command instance
    public CommandRouter(Map<String, Command> commands /* Multibinding into Map */) {
        // This map contains:
        // "hello" -> HelloWorldCommand
        // "login" -> LoginCommand
        this.commands = commands;
    }

    Result route(String input) {
        List<String> splitInput = split(input);
        if (splitInput.isEmpty()) return invalidCommand(input);

        String commandKey = splitInput.get(0);
        Command command = commands.get(commandKey);
        if (command == null) return invalidCommand(input);

        Result result = command.handleInput(splitInput.subList(1, splitInput.size()));
        if (result.status() == Status.INVALID) {
            Log.e(LOG_TAG, commandKey + ": invalid arguments");
        }
        return result;
    }

    private Result invalidCommand(String input) {
        Log.e(LOG_TAG, String.format("Couldn't understand '%s'. Please try again.", input));
        return Result.invalidResult();
    }

    private static List<String> split(String string) {
        return Arrays.asList(string.split(" "));
    }
}

final class CommandProcessor {
    private final Deque<CommandRouter> commandRouterStack = new ArrayDeque<>();

    /**
     * First CommandRouter is user-unaware CommandRouter with LoginCommand and HelloWorldCommand
     */
    @Inject // Inject Dependency on CommandRouter
    CommandProcessor(CommandRouter firstCommandRouter) {
        commandRouterStack.push(firstCommandRouter);
    }

    Status process(String input) {
        Result result = commandRouterStack.peek().route(input);  // Execute input
        if (result.status().equals(Status.INPUT_COMPLETED)) {
            // Pop top CommandRouter when it's completed
            commandRouterStack.pop();
            return commandRouterStack.isEmpty() ? Status.INPUT_COMPLETED : Status.HANDLED;
        }

        result.nestedCommandRouter().ifPresent(
                // x is nestedCommandRouter when it presents
                x -> commandRouterStack.push(x)
                );
        return result.status();
    }
}

/**
 * Dagger will generate an implementation DaggerCommandRouterFactory of this interface.
 * Dagger will create a static create() method to give us an instance of the class
 */
@Component(modules = {
        HelloWorldModule.class /* Tell Dagger to look for @Binds method in HelloWorldModule */,
        LoginCommandModule.class, /* Tell Dagger to use LoginModule as instructions */
        LoggingOutModule.class, /* Tell Dagger to use LoggingOutModule as instructions */
        UserCommandsRouter.InstallationModule.class, /* Tell Dagger to user UserCommandsRouter.InstallationModule as instructions */
})
@Singleton
interface CommandProcessorFactory {
    /**
     * Request a CommandProcessor instance
     */
    CommandProcessor processor();
}
