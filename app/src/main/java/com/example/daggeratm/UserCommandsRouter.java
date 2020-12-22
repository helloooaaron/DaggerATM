package com.example.daggeratm;

import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;
import com.example.daggeratm.Database.Account;

@PerSession
@Subcomponent(modules = {  // Subcomponent inherited the modules from parent component
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
         * Dagger that the Account instance is requestable by any binding methods in this
         * component.
         * In another word, @BindsInstance binds the instance of Account that is constructed outside of the graph
         * into this subcomponent
         */
        UserCommandsRouter create(@BindsInstance Account account);
    }

    // Module tells Dagger how to provide UserCommandsRouter when requested in @Component
    @Module(subcomponents = UserCommandsRouter.class)
    interface InstallationModule {}
}
