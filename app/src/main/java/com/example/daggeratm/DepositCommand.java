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

import com.example.daggeratm.Database.Account;

public final class DepositCommand implements Command {
    private static final String LOG_TAG = DepositCommand.class.getSimpleName();
    private final Account account;
    private final Outputter outputter;

    @Inject
    public DepositCommand(Account account, Outputter outputter) {
        this.account = account;
        this.outputter = outputter;
        outputter.output(LOG_TAG, "Creating a new " + this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Result handleInput(List<String> input) {
        account.deposit(new BigDecimal(input.get(1)));
        outputter.output(LOG_TAG, account.username() + " now has: " + account.balance());
        return Result.handled();
    }
}

@Module // Modules are collections of binding methods that give Dagger instructions on how to construct dependencies.
abstract class DepositCommandModule {

    @Binds // An instruction that tells Dagger to bind @paramType DepositCommand to @returnType Command when a Command is requested.
    @IntoMap
    @StringKey("deposit")
    abstract Command depositCommand(DepositCommand command);
}
