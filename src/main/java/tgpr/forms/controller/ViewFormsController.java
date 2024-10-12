package tgpr.forms.controller;

import tgpr.forms.view.LoginView;
import tgpr.forms.view.ViewFormsView;
import tgpr.framework.Controller;

import java.awt.*;

public class ViewFormsController extends Controller<ViewFormsView> {
    public ViewFormsView getView() {
        return new ViewFormsView(this);
    }
}
