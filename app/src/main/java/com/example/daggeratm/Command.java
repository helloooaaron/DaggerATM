package com.example.daggeratm;

import android.widget.TextView;

import java.util.List;

public interface Command {
    /**
     * String token that signifes this command should be selected
     */
    String key();

    /**
     * Process the rest of the command's words and do something
     * @param input
     * @return
     */
    Status handleInput(List<String> input);

    enum Status {
        INVALID,
        HANDLED,
    }
}

abstract class SingleArgCommand implements Command {

    @Override
    public Status handleInput(List<String> input) {
        return input.size() == 1 ? handleArg(input.get(0)) : Status.INVALID;
    }

    protected abstract Status handleArg(String arg);
}
