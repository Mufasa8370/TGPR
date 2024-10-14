package tgpr.forms.controller;

import tgpr.forms.FormsApp;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.LoginView;
import tgpr.framework.Controller;
import tgpr.framework.Error;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;

import java.util.List;
import java.util.Objects;

public class LoginController extends Controller<LoginView> {
    public void exit() {
        System.exit(0);
    }

    public boolean login(String pseudo, String password) {
        var errors = new ErrorList();
        //Vérifier si email existe !!!!!PEUT ETRE METTRE DANS MODEL
        if (!Objects.equals(pseudo, "guest@epfc.eu")) {
            List<User> users = User.getAll();
            boolean emailExists = users.stream().anyMatch(user -> user.getEmail().equals(pseudo));
            if(!emailExists){
                errors.add(new Error("The email address does not exist"));
            }
        }

        if (errors.isEmpty()) {
            var member = User.checkCredentials(pseudo, password);
            if (member != null) {
                Security.login(member);
                navigateTo(new ViewFormsController());
                //showMessage("Connexion réussie avec user : "+ member.getFullName() +" role -->"+member.getRole()+", le use case view_forms est en préparation :)","Info","Close");
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
        Controller.navigateTo(new SignupController());
    }

    @Override
    public LoginView getView() {
        return new LoginView(this);
    }
}