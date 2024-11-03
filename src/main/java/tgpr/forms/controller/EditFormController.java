package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.view.EditFormView;
import tgpr.framework.Controller;

public class EditFormController  extends Controller<EditFormView> {

    private final EditFormView view;
    private Form form;
    private final boolean isNew;

    public EditFormController(){
        this(null);
    }
    public EditFormController(Form form) {
        this.form = form;
        this.isNew = form == null;
        view = new EditFormView(this,form);
    }
    @Override
    public EditFormView getView() {
        return view;
    }
}
