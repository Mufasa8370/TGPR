package tgpr.forms.controller;

import tgpr.forms.FormsApp;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.LoginView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;

import java.util.List;

public class LoginController extends Controller<LoginView> {
    public void exit() {
        System.exit(0);
    }

    public boolean login(String pseudo, String password) {
        var errors = new ErrorList();

        if (errors.isEmpty()) {
            var member = User.checkCredentials(pseudo, password);
            if (member != null) {
                Security.login(member);
                //navigateTo(new TestController());
                showMessage("Connexion réussie, le use case view_forms est en préparation :)","Info","Close");
            } else
                showError(String.valueOf(new Error("invalid credentials")));
        } else
            showErrors(errors);
        return false;
    }

    public void seedData() {
        Model.seedData(FormsApp.DATABASE_SCRIPT_FILE);
    }

    public void signup(){
        showMessage("Le use case signup est en préparation :)","Info","Close");
    }

    @Override
    public LoginView getView() {
        return new LoginView(this);
    }
}