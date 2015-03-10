package bytehala.flowmortarexample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import javax.inject.Inject;

import bytehala.flowmortarexample.screen.MainScreen;
import mortar.dagger1support.ObjectGraphService;

/**
 * Created by Lem on 3/8/2015.
 */
public class MainView extends FrameLayout {

    @Inject
    MainScreen.Presenter presenter;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ObjectGraphService.inject(context, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }


}
