package tgpr.forms.controller;
import tgpr.forms.view.FormEditConfirmationView;
import tgpr.framework.Controller;

public class FormEditConfirmationController extends Controller<FormEditConfirmationView> {
    public boolean answer = false;

    public FormEditConfirmationController(){

    }
    @Override
    public FormEditConfirmationView getView() {
        return new FormEditConfirmationView(this);
    }
}
