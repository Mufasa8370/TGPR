package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.EditProfileView;
import tgpr.forms.view.ViewFormsView;
import tgpr.forms.view.ViewProfileView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

public class EditProfileController extends Controller<EditProfileView> {
    private final EditProfileView view;

    @Override
    public EditProfileView getView() { return view; }

    public EditProfileController() {
        view = new EditProfileView(this);
    }

    public void save(String email, String fullName) {
        var errors = validate(email, fullName);
        if (errors.isEmpty()) {
            // sauvegarde
            Security.getLoggedUser().setFullName(fullName);
            Security.getLoggedUser().setEmail(email);
            Security.getLoggedUser().save();

            // refreshing des views
            ViewProfileView.getInstance().refreshNameAndEmail();
            ViewFormsView.getInstance().refreshTitle();

            // fermeture de la vue
            view.close();
        }
    }

    public ErrorList validate(String email, String fullName) {
        var errors = new ErrorList();

        // Vérifier que le nom contient au moins 3 caractères
        if (fullName.length() < 3)
            errors.add("minimum 3 char", User.Fields.FullName);

        // Vérifier que l'email a un format valide (ex: exemple@domaine.com)
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            errors.add("invalid email", User.Fields.Email);

        // Vérifier que l'email n'existe pas déjà
        if (User.getByEmail(email) != null && !email.equals(Security.getLoggedUser().getEmail()))
            errors.add("email already exists", User.Fields.Email);

        return errors;
    }
}
