package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.ChangePasswordView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import static tgpr.framework.Tools.hash;

public class ChangePasswordController extends Controller<ChangePasswordView> {
    private final ChangePasswordView view;

    @Override
    public ChangePasswordView getView() { return view; }

    public ChangePasswordController() { view = new ChangePasswordView(this); }

    public void save(String oldPassword, String password, String confirmPassword) {
        var errors = validate(oldPassword, password, confirmPassword);
        if (errors.isEmpty()) {
            // sauvegarde
            var hashedPassword = password.isBlank() ? password : hash(password);
            User user = Security.getLoggedUser();
            user.setPassword(hashedPassword);
            user.save();

            // fermeture de la vue
            view.close();
        }
    }

    public ErrorList validate(String oldPassword, String password, String confirmPassword) {
        var errors = new ErrorList();

        // Vérifier que l'ancien mot de passe est correct
        if (!Security.getLoggedUser().getPassword().equals(hash(oldPassword))) {
            errors.add("incorrect password", User.Fields.OldPassword);
        }

        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (password.equals(oldPassword)) {
            errors.add("must be different", User.Fields.Password);
        }

        // Vérifier que le mot de passe et la confirmation correspondent
        if (!password.equals(confirmPassword)) {
            errors.add("must match password", User.Fields.ConfirmPassword);
        }

        // Vérifier la longueur du mot de passe
        if (password.length() < 8) {
            errors.add("minimum 8 char", User.Fields.Password);
        }

        // Vérifier que le mot de passe contient un chiffre
        if (!password.matches(".*\\d.*")) {
            errors.add("must contain a digit", User.Fields.Password);
        }

        // Vérifier que le mot de passe contient une lettre majuscule
        if (!password.matches(".*[A-Z].*")) {
            errors.add("must contain an uppercase letter", User.Fields.Password);
        }

        // Vérifier que le mot de passe contient un caractère non alphanumérique
        if (!password.matches(".*\\W.*")) {
            errors.add("must contain a non-alphanumeric character", User.Fields.Password);
        }

        return errors;
    }


}
