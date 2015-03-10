package bytehala.flowmortarexample.screen;

import android.os.Bundle;
import android.support.annotation.IdRes;

import javax.inject.Inject;
import javax.inject.Singleton;

import bytehala.flowmortarexample.R;
import bytehala.flowmortarexample.core.RootModule;
import bytehala.flowmortarexample.mortarscreen.WithModule;
import bytehala.flowmortarexample.view.MainView;
import flow.Flow;
import flow.Layout;
import flow.Path;
import mortar.ViewPresenter;

/**
* Created by Lem on 3/10/2015.
*/
@Layout(R.layout.screen_main) @WithModule(MainScreen.Module.class)
public class MainScreen extends Path {

    @dagger.Module(injects = MainView.class, addsTo = RootModule.class)
    public static class Module {
        //TODO: ADD @PROVIDES HERE
    }

    @Singleton
    public static class Presenter extends ViewPresenter<MainView> {

        @Inject
        Presenter() {
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(!hasView()) return;
            // view-related setters here
        }

    }
}
