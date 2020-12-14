package com.example.daggeratm;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

public final class LoginCommand extends SingleArgCommand {
    private static final String LOG_TAG = LoginCommand.class.getSimpleName();
    private final Database database;
    private final Outputter outputter;

    @Inject
    public LoginCommand(Database database, Outputter outputter) {
        this.database = database;
        this.outputter = outputter;
        outputter.output(LOG_TAG, "Creating a new " + this);
    }

    @Override
    public String key() {
        return "login";
    }

    @Override
    protected Status handleArg(String username) {
        Database.Account account = database.getAccount(username);
        outputter.output(LOG_TAG, username + " is logged in with balance: " + account.balance());
        return Status.HANDLED;
    }
}

@Module // Modules are collections of binding methods that give Dagger instructions on how to construct dependencies.
abstract class LoginCommandModule {

    @Binds // An instruction that tells Dagger to bind @paramType LoginCommand to @returnType Command when a Command is requested.
    // !!Multibindings!! Combine to allow for the creation of a Map<@StringKey, @returnType>
    @IntoMap
    @StringKey("login") // Key for the entry populated to the map
    abstract Command loginCommand(LoginCommand command);
}
