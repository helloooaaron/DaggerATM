package com.example.daggeratm;

import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton // It's really another @Scope annotation. The lifetime of this instance is bounded with the lifetime of the component(CommandProcessorFactory) annotated with @Singleton
public final class Database {
    private final Map<String, Account> accounts = new HashMap<>();

    @Inject
    Database() {
        Log.i("Database", "In constructor");
    }

    Account getAccount(String username) {
        return accounts.computeIfAbsent(username, k -> new Account(k));
    }

    static final class Account {
        private final String username;
        private BigDecimal balance = BigDecimal.ZERO;

        public Account(String username) {
            this.username = username;
        }

        String username() {
            return username;
        }

        BigDecimal balance() {
            return balance;
        }

        void deposit(BigDecimal amount) {
            balance = balance.add(amount);
        }

        public void withdraw(BigDecimal amount) {
            balance = balance.subtract(amount);
        }
    }
}

