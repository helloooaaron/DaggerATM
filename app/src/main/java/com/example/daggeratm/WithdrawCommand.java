package com.example.daggeratm;

import com.example.daggeratm.Database.Account;

import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Qualifier;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class WithdrawCommand implements Command {
    private static final String LOG_TAG = WithdrawCommand.class.getSimpleName();
    private final Account account;
    private final Outputter outputter;
    private final BigDecimal minBalance;
    private final BigDecimal maxWithdraw;

    @Inject
    public WithdrawCommand(Account account, Outputter outputter,
                           @MinBalance BigDecimal minBalance, // Use @Qualifer to diff different things of same Java type
                           @MaxWithdraw BigDecimal maxWithdraw) {
        this.account = account;
        this.outputter = outputter;
        this.minBalance = minBalance;
        this.maxWithdraw = maxWithdraw;
        outputter.output(LOG_TAG, "Creating a new " + this);
    }

    @Override
    public Result handleInput(List<String> input) {
        BigDecimal amount = new BigDecimal(input.get(0));
        if (amount.compareTo(maxWithdraw) > 0) {
            return Result.invalidResult();
        }

        BigDecimal newBalance = account.balance().subtract(amount);
        if (newBalance.compareTo(minBalance) < 0) {
            outputter.output(LOG_TAG, "Don't have enough money to withdraw");
            return Result.invalidResult();
        } else {
            account.withdraw(amount);
            outputter.output(LOG_TAG, "New balance is " + account.balance());
        }
        return Result.handled();
    }
}

@Module
interface WithdrawCommandModule {
    @Binds
    @IntoMap
    @StringKey("withdraw")
    public Command withdrawCommand(WithdrawCommand command);
}

@Qualifier // Define qualifier for different things of same Java type in an @Inject
@Retention(RUNTIME)
@interface MinBalance {} // Qualifer @MinBalance

@Qualifier // Define qualifier for different things of same Java type in an @Inject
@Retention(RUNTIME)
@interface MaxWithdraw {} // Qualifer @MaxWithdraw

@Module
interface AmountsModule {
    @Provides
    @MinBalance // Tell Dagger to provide BigDecimal when @MinBalance BigDecimal instance is requested.
    static BigDecimal minBalance() {
        return BigDecimal.ZERO;
    }

    @Provides
    @MaxWithdraw // Tell Dagger to provide BigDecimal when @MaxWithdraw BigDecimal instance is requested.
    static BigDecimal maxWithdraw() {
        return new BigDecimal(25);
    }
}
