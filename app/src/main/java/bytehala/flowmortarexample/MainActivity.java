package bytehala.flowmortarexample;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import bytehala.flowmortarexample.screen.MainScreen;
import flow.ActivityFlowSupport;
import flow.Backstack;
import flow.Flow;
import flow.Parceler;
import flow.Path;
import flow.PathContainerView;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;
import mortar.dagger1support.ObjectGraphService;

import static mortar.bundler.BundleServiceRunner.getBundleServiceRunner;


public class MainActivity extends Activity implements Flow.Dispatcher{

    @Inject
    Parceler parceler;

    private MortarScope activityScope;
    private PathContainerView container;
    private ActivityFlowSupport flowSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleServiceRunner(this).onCreate(savedInstanceState);

        ObjectGraphService.inject(this, this);
        setupFlowSupport(savedInstanceState);

        setContentView(R.layout.activity_main);
        container = (PathContainerView) findViewById(R.id.container);
    }

    private void setupFlowSupport(Bundle savedInstanceState) {
        Backstack defaultBackstack = Backstack.single(new MainScreen());
        @SuppressWarnings("deprecation") ActivityFlowSupport.NonConfigurationInstance nonConfig =
                (ActivityFlowSupport.NonConfigurationInstance) getLastNonConfigurationInstance();
        flowSupport =
                ActivityFlowSupport.onCreate(nonConfig, savedInstanceState, parceler, defaultBackstack);
    }

    @Override public Object getSystemService(String name) {
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
    @Override public Object onRetainNonConfigurationInstance() {
        return flowSupport.onRetainNonConfigurationInstance();
    }

    @Override protected void onResume() {
        super.onResume();
        flowSupport.onResume(this);
    }

    @Override protected void onPause() {
        flowSupport.onPause();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
        Path path = traversal.destination.current();
        setTitle(path.getClass().getSimpleName());
//        boolean canGoBack = traversal.destination.size() > 1;
//        actionBar.setDisplayHomeAsUpEnabled(canGoBack);
//        actionBar.setHomeButtonEnabled(canGoBack);
        container.dispatch(traversal, new Flow.TraversalCallback() {
            @Override public void onTraversalCompleted() {
//                invalidateOptionsMenu();
                callback.onTraversalCompleted();
            }
        });
    }
}
