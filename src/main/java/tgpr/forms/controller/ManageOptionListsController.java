package tgpr.forms.controller;

import tgpr.forms.view.ManageOptionListsView;
import tgpr.framework.Controller;


public class ManageOptionListsController extends Controller<ManageOptionListsView>{
    public ManageOptionListsView getView() {
        return new ManageOptionListsView(this);
    }
}


