package com.example.daggeratm;

import java.util.List;
import java.util.Optional;

public interface Command {
    /**
     * Process the rest of the command's words and do something
     */
    Result handleInput(List<String> input);

    class Result {
        private final Status status;
        private final Optional<CommandRouter> nestedCommandRouter;

        public Result(Status status, Optional<CommandRouter> nestedCommandRouter) {
            this.status = status;
            this.nestedCommandRouter = nestedCommandRouter;
        }

        static Result enterNestedCommandSet(CommandRouter nestedCommandRouter) {
            return new Result(Status.HANDLED, Optional.of(nestedCommandRouter));
        }

        static Result invalidResult() {
            return new Result(Status.INVALID, Optional.empty());
        }

        static Result handled() {
            return new Result(Status.HANDLED, Optional.empty());
        }

        static Result inputCompleted() {
            return new Result(Status.INPUT_COMPLETED, Optional.empty());
        }

        public Status status() { return status; }

        public Optional<CommandRouter> nestedCommandRouter() { return nestedCommandRouter; }
    }

    enum Status {
        INVALID, HANDLED, INPUT_COMPLETED,
    }
}

abstract class SingleArgCommand implements Command {

    @Override
    public Result handleInput(List<String> input) {
        return input.size() == 1 ? handleArg(input.get(0)) : Result.invalidResult();
    }

    protected abstract Result handleArg(String arg);
}
