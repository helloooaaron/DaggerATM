package com.example.daggeratm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

public final class DepositCommand implements Command {
    private static final String LOG_TAG = DepositCommand.class.getSimpleName();
    private final Outputter outputter;
    private final Database database;

    @Inject
    public DepositCommand(Database database, Outputter outputter) {
        this.database = database;
        this.outputter = outputter;
        outputter.output(LOG_TAG, "Creating a new " + this);
    }

    @Override
    public String key() {
        return "deposit";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Status handleInput(List<String> input) {
        if (input.size() != 2) return Status.INVALID;
        Database.Account account = database.getAccount(input.get(0));
        account.deposit(new BigDecimal(input.get(1)));
        outputter.output(LOG_TAG, account.username() + " now has: " + account.balance());
        return Status.HANDLED;
    }
}

@Module // Modules are collections of binding methods that give Dagger instructions on how to construct dependencies.
abstract class DepositCommandModule {

    @Binds // An instruction that tells Dagger to bind @paramType DepositCommand to @returnType Command when a Command is requested.
    @IntoMap
    @StringKey("deposit")
    abstract Command depositCommand(DepositCommand command);
}
