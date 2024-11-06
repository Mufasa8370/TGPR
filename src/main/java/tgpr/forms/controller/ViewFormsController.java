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
        showMessage("Le use case view_profile est en préparation :)","Info","Close");
    }

    public void createNewForm() {
        navigateTo(new EditFormController());
    }

    public void SharesForm(){
        navigateTo(new ManageSharesController());
    }
    public void manageYourOptionList() {
        //navigateTo(AddEditOptionListController());
    }

    public void manageYourDistributionList() {

        Form form = Form.getByKey(17);
        Controller.navigateTo(new EditFormController(form));
    }
}
