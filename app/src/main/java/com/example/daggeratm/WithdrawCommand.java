package com.example.daggeratm;

import com.example.daggeratm.Database.Account;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Scope;

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
    private final WithdrawalLimiter withdrawalLimiter;
    private final BigDecimal minBalance;
    private final BigDecimal maxWithdraw;

    @Inject
    public WithdrawCommand(Account account, Outputter outputter,
                           WithdrawalLimiter withdrawalLimiter,
                           // Use @Qualifer to diff different things of same Java type
                           @MinBalance BigDecimal minBalance,
                           @MaxWithdraw BigDecimal maxWithdraw) {
        this.account = account;
        this.outputter = outputter;
        this.withdrawalLimiter = withdrawalLimiter;
        this.minBalance = minBalance;
        this.maxWithdraw = maxWithdraw;
        outputter.output(LOG_TAG, "Creating a new " + this);
    }

    @Override
    public Result handleInput(List<String> input) {
        BigDecimal amount = new BigDecimal(input.get(0));
        BigDecimal remainingWithdrawalLimit = withdrawalLimiter.remainingWithdrawalLimit();
        if (amount.compareTo(remainingWithdrawalLimit) > 0) {
            outputter.output(
                    LOG_TAG,
                    String.format("You may not withdraw %s; you may withdraw %s more in this session",
                    amount, remainingWithdrawalLimit)
            );
            return Result.invalidResult();
        }

        BigDecimal newBalance = account.balance().subtract(amount);
        if (newBalance.compareTo(minBalance) < 0) {
            outputter.output(LOG_TAG, "Don't have enough money to withdraw");
            return Result.invalidResult();
        } else {
            account.withdraw(amount);
            withdrawalLimiter.recordWithdrawal(amount);
            outputter.output(LOG_TAG, "New balance is " + account.balance());
        }
        return Result.handled();
    }
}

// Maximum withdrawal amount for a session
@PerSession
final class WithdrawalLimiter {
    private static final String LOG_TAG = WithdrawalLimiter.class.getSimpleName();
    private BigDecimal remainingWithdrawalLimit;
    private final Outputter outputter;

    @Inject
    public WithdrawalLimiter(
            @MaxWithdraw  BigDecimal remainingWithdrawalLimit,  // Init(injected) with max withdraw cap
            Outputter outputter
            ) {
        this.remainingWithdrawalLimit = remainingWithdrawalLimit;
        this.outputter = outputter;
    }

    final BigDecimal remainingWithdrawalLimit() {
        return remainingWithdrawalLimit;
    }

    void recordDeposit(BigDecimal amount) {
        remainingWithdrawalLimit = remainingWithdrawalLimit.add(amount);
        outputter.output(LOG_TAG, "New withdraw limit: " + remainingWithdrawalLimit);
    }

    void recordWithdrawal(BigDecimal amount) {
        remainingWithdrawalLimit = remainingWithdrawalLimit.subtract(amount);
        outputter.output(LOG_TAG, "New withdraw limit: " + remainingWithdrawalLimit);
    }
}

@Module
interface WithdrawCommandModule {
    @Binds
    @IntoMap
    @StringKey("withdraw")
    Command withdrawCommand(WithdrawCommand command);
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
    @MinBalance // Tell Dagger how to provide @MinBalance BigDecimal instance when requested
    static BigDecimal minBalance() {
        return BigDecimal.ZERO;
    }

    @Provides
    @MaxWithdraw // Tell Dagger how to provide @MaxWithdraw BigDecimal instance when requested
    static BigDecimal maxWithdraw() {
        return new BigDecimal(25);
    }
}

@Scope // Instruct Dagger to provide one shared instance for all the requests for that type within an instance of the (sub)component that shares the same annotation.
       // The lifetime of a scoped instance is directly related to the lifetime of the (sub)component(UserCommandsRouter) annotated with that scope
@Documented
@Retention(RUNTIME)
@interface PerSession {}
