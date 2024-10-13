package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.view.ViewFormsView;
import tgpr.framework.Controller;

import static tgpr.forms.model.Security.getLoggedUser;


public class ViewFormsController extends Controller<ViewFormsView> {
    public ViewFormsView getView() {
        return new ViewFormsView(this);
    }

    public void exit() {
        System.out.println("Exit");
        System.exit(0);
    }

    public void logout(){
        Security.logout();
        navigateTo(new LoginController());
    }

    public void menuViewProfile() {
        showMessage("Le use case view_profile est en préparation :)","Info","Close");
    }

    public void createNewForm() {
        showMessage("Le use case add_edit_form est en préparation :)","Info","Close");
    }
}
