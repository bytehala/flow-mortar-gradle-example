package bytehala.flowmortarexample;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import javax.inject.Inject;

import bytehala.flowmortarexample.android.ActionBarOwner;
import bytehala.flowmortarexample.pathview.HandlesBack;
import bytehala.flowmortarexample.screen.ChatListScreen;
import bytehala.flowmortarexample.screen.FriendListScreen;
import bytehala.flowmortarexample.screen.MainScreen;
import flow.ActivityFlowSupport;
import flow.Backstack;
import flow.Flow;
import flow.HasParent;
import flow.Parceler;
import flow.Path;
import flow.PathContainerView;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;
import mortar.bundler.BundleServiceRunner;
import mortar.dagger1support.ObjectGraphService;
import rx.functions.Action0;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static mortar.bundler.BundleServiceRunner.getBundleServiceRunner;


public class MainActivity extends Activity implements Flow.Dispatcher, ActionBarOwner.Activity {

    @Inject
    Parceler parceler;

    private ActionBarOwner.MenuAction actionBarMenuAction;
    @Inject
    ActionBarOwner actionBarOwner;

    private MortarScope activityScope;
    private PathContainerView container;
    private ActivityFlowSupport flowSupport;
    private HandlesBack containerAsHandlesBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleServiceRunner(this).onCreate(savedInstanceState);

        ObjectGraphService.inject(this, this);
        setupFlowSupport(savedInstanceState);


        setContentView(R.layout.activity_main);

        actionBarOwner.takeView(this);
        container = (PathContainerView) findViewById(R.id.container);
        containerAsHandlesBack = (HandlesBack) container;
    }

    private void setupFlowSupport(Bundle savedInstanceState) {
        Backstack defaultBackstack = Backstack.single(new ChatListScreen());
        @SuppressWarnings("deprecation") ActivityFlowSupport.NonConfigurationInstance nonConfig =
                (ActivityFlowSupport.NonConfigurationInstance) getLastNonConfigurationInstance();
        flowSupport =
                ActivityFlowSupport.onCreate(nonConfig, savedInstanceState, parceler, defaultBackstack);
    }

    @Override
    public Object getSystemService(String name) {
        Object service = null;
        if (flowSupport != null) {
            service = flowSupport.getSystemService(name);
        }
        MortarScope activityScope = MortarScope.findChild(getApplicationContext(), getScopeName());

        if (activityScope == null) {
            activityScope = MortarScope.buildChild(getApplicationContext(), getScopeName()) //
                    .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                    .build();
        }

        return activityScope.hasService(name) ? activityScope.getService(name)
                : service != null ? service : super.getSystemService(name);
    }


    private String getScopeName() {
        // This used to be getLocalClassName(), but for some reason it crashes when I do that
        return "MainActivity.class" + "-task-" + getTaskId();
    }

    @SuppressWarnings("deprecation") // https://code.google.com/p/android/issues/detail?id=151346
    @Override
    public Object onRetainNonConfigurationInstance() {
        return flowSupport.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowSupport.onResume(this);
    }

    @Override
    protected void onPause() {
        flowSupport.onPause();
        super.onPause();
    }


    /**
     * Configure the action bar menu as required by {@link ActionBarOwner.Activity}.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuAction != null) {
            menu.add(actionBarMenuAction.title)
                    .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            actionBarMenuAction.action.call();
                            return true;
                        }
                    });
        }
        menu.add("Log Scope Hierarchy")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d("DemoActivity", MortarScopeDevHelper.scopeHierarchyToString(activityScope));
                        return true;
                    }
                });
        return true;
    }

    /** Inform the view about up events. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Inform the view about back events. */
    @Override public void onBackPressed() {
        if (!containerAsHandlesBack.onBackPressed()) super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        actionBarOwner.dropView(this);

        // activityScope may be null in case isWrongInstance() returned true in onCreate()
        if (isFinishing() && activityScope != null) {
            activityScope.destroy();
            activityScope = null;
        }

        super.onDestroy();
    }

    @Override
    public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
        Path newScreen = traversal.destination.current();
        boolean hasUp = newScreen instanceof HasParent;
        String title = newScreen.getClass().getSimpleName();
        ActionBarOwner.MenuAction menu =
                hasUp ? null : new ActionBarOwner.MenuAction("Friends", new Action0() {
                    @Override public void call() {
                        Flow.get(MainActivity.this).goTo(new FriendListScreen());
                    }
                });
        actionBarOwner.setConfig(new ActionBarOwner.Config(false, hasUp, title, menu));

        container.dispatch(traversal, callback);
    }


    @Override
    public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override
    public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(enabled);
            actionBar.setHomeButtonEnabled(enabled);
        }
    }

    @Override
    public void setMenu(ActionBarOwner.MenuAction action) {
        if (action != actionBarMenuAction) {
            actionBarMenuAction = action;
            invalidateOptionsMenu();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

}
