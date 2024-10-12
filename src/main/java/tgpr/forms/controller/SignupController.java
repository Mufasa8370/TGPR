package tgpr.forms.controller;

import tgpr.forms.view.SignupView;
import tgpr.framework.Controller;

public class SignupController extends Controller<SignupView> {
    private final SignupView view;

    @Override
    public SignupView getView() {
        return view;
    }

    public SignupController() {
        view = new SignupView(this);
    }

    public void signup() {

    }

}
