package com.example.daggeratm;

import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;
import com.example.daggeratm.Database.Account;

@Subcomponent(modules = {
        DepositCommandModule.class,
        WithdrawCommandModule.class,
        LogoutCommandModule.class,
        AmountsModule.class,
})
public interface UserCommandsRouter {
    CommandRouter router();

    @Subcomponent.Factory // Create instances of the subcompnent.
    interface Factory {
        /**
         * Create a UserCommandsRouter instance associated with an account, @BindsInstance tells
         * Dagger that the Account instance should be requestable by any binding methods in this
         * component.
         */
        UserCommandsRouter create(@BindsInstance Account account);
    }

    // Module tells Dagger how to provide UserCommandsRouter when requested in @Component
    @Module(subcomponents = UserCommandsRouter.class)
    interface InstallationModule {}
}
