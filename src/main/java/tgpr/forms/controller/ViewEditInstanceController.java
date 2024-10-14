package tgpr.forms.controller;

import tgpr.forms.model.Instance;
import tgpr.forms.view.SignupView;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.framework.Controller;

public class ViewEditInstanceController extends Controller<ViewEditInstanceView> {
    private final ViewEditInstanceView view;
    private Instance instance;


    @Override
    public ViewEditInstanceView getView() {
        return view;
    }

    public ViewEditInstanceController(Instance i) {
        this.instance = i;
        view = new ViewEditInstanceView(this, i);
    }
    public ViewEditInstanceController() {
        view = new ViewEditInstanceView(this);
    }

    public ViewEditInstanceController(Instance i, boolean viewSubmit) {
        view = new ViewEditInstanceView(this, i, viewSubmit);
    }

    public void viewSubmission() {
        navigateTo(new ViewEditInstanceController(instance,true));

    }

    public void submitAgain() {
        navigateTo(new ViewEditInstanceController());

    }

    public void cancel() {
        navigateTo(new ViewFormsController());
    }
}
