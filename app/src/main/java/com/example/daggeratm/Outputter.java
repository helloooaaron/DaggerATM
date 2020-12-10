package com.example.daggeratm;

import android.util.Log;

import dagger.Module;
import dagger.Provides;

public interface Outputter {
    void output(String tag, String output);
}

@Module // Instructions on how to construct Outputter type
abstract class LoggingOutModule {

    /*
     * Concrete method in a module that tells Dagger to call this method when an instance of @returnType is requested.
     * It doesn't need to create a new instance on each invocation, this is the impl detail of this method.
     * Like @Inject constructor, the method can have parameters, which are its dependencies.
     */
    @Provides
    static Outputter loggingOutputter() {
        return (tag, msg) -> Log.i(tag, msg);
    }
}
