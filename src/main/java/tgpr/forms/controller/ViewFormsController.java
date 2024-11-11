package tgpr.forms.controller;

import tgpr.forms.model.Form;
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
        navigateTo(new ViewProfileController());
    }

    public void createNewForm() {
        navigateTo(new EditFormController());
    }

    public void SharesForm(){
        ManageSharesController.SharesForm();
    }

    public void manageYourOptionList() {
        //navigateTo(AddEditOptionListController());
    }

    public void manageYourDistributionList() {
        navigateTo(new ManageDistListsController());
    }
}
