package com.example.daggeratm;

import java.util.List;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

public class HelloWorldCommand implements Command {
    private static final String LOG_TAG = HelloWorldCommand.class.getSimpleName();

    private final Outputter outputter;

    @Inject // Tell Dagger to call (and how) this constructor when we ask for a HelloWorldCommand
    public HelloWorldCommand(Outputter outputter) {
        this.outputter = outputter;
    }

    @Override
    public String key() {
        return "hello";
    }

    @Override
    public Status handleInput(List<String> input) {
        if (input.isEmpty()) return Status.INVALID;
        outputter.output(LOG_TAG, "world!");
        return Status.HANDLED;
    }
}

@Module // Modules are collections of binding methods that give Dagger instructions on how to construct dependencies.
abstract class HelloWorldModule {

    @Binds // An instruction that tells Dagger to bind @paramType HelloWorldCommand to @returnType Command when a Command is requested.
    // !!Multibindings!! Combine to allow for the creation of a Map<@StringKey, @returnType>
    @IntoMap
    @StringKey("hello") // Key for the entry populated to the map
    abstract Command /* Type Dagger doesn't yet know how to construct */
    helloWorldCommand(HelloWorldCommand /* Type Dagger already knows how to construct */ command);
}
