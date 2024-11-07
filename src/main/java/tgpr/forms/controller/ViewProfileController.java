package tgpr.forms.controller;

import tgpr.forms.view.ViewProfileView;
import tgpr.framework.Controller;

public class ViewProfileController extends Controller<ViewProfileView> {
    private final ViewProfileView view;

    @Override
    public ViewProfileView getView() { return view; }

    public ViewProfileController() {
        view = new ViewProfileView(this);
    }
}
