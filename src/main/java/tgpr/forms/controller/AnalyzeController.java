package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.view.AnalyzeView;
import tgpr.framework.Controller;
import tgpr.forms.model.Instance;

public class AnalyzeController extends Controller<AnalyzeView> {
    private final AnalyzeView view;
    private final Form form;

    @Override
    public AnalyzeView getView() { return view; }

    public AnalyzeController(Form form) {
        this.form = form;
        view = new AnalyzeView(this, form);
    }

    public long getNbSubmittedInstances() {
        return form.getInstances().stream().filter(Instance::isCompleted).count();
    }

}
