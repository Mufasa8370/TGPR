package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.ViewProfileView;
import tgpr.framework.Controller;

public class ViewProfileController extends Controller<ViewProfileView> {
    private final ViewProfileView view;

    @Override
    public ViewProfileView getView() { return view; }

    public ViewProfileController() {
        Security.login(User.getByKey(1));
        view = new ViewProfileView(this);
    }


}
