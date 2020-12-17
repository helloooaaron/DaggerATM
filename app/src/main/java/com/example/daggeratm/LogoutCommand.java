package com.example.daggeratm;

import java.util.List;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

public class LogoutCommand implements Command {
    @Inject
    public LogoutCommand() { }

    @Override
    public Result handleInput(List<String> input) {
        return input.isEmpty() ? Result.inputCompleted() : Result.invalidResult();
    }
}

@Module
interface LogoutCommandModule {
    @Binds
    @IntoMap
    @StringKey("logout")
    public Command logoutCommand(LogoutCommand command);
}
