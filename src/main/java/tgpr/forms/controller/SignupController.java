package tgpr.forms.controller;

import tgpr.forms.model.Security;
import tgpr.forms.model.User;
import tgpr.forms.view.SignupView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import static tgpr.forms.model.User.getByEmail;
import static tgpr.framework.Tools.hash;

public class SignupController extends Controller<SignupView> {
    private final SignupView view;

    @Override
    public SignupView getView() {
        return view;
    }

    public SignupController() {
        view = new SignupView(this);
    }

    public void save(String email, String fullName, String password, String confirmPassword) {
        var errors = validate(email, fullName, password, confirmPassword);
        if (errors.isEmpty()) {
            var hashedPassword = password.isBlank() ? password : hash(password);
            User user = new User(fullName, email, hashedPassword, User.Role.User);
            user.save();
            Security.login(user);
            navigateTo(new ViewFormsController());
        } else
            showErrors(errors);
    }

    public static boolean emailExists(String email) {
        return getByEmail(email) != null;
    }

    public ErrorList validate(String email, String fullName, String password, String confirmPassword) {
        var errors = new ErrorList();

        // Vérifier que le nom complet contient au moins 3 caractères
        if (fullName.length() < 3)
            errors.add("minimum 3 char", User.Fields.FullName);

        // Vérifier que l'email n'existe pas déjà dans la base de données
        if (emailExists(email))
            errors.add("already exists", User.Fields.Email);

        // Vérifier que l'email a un format valide (ex: exemple@domaine.com)
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            errors.add("invalid email", User.Fields.Email);

        // Vérifier que le mot de passe et sa confirmation correspondent
        if (!password.equals(confirmPassword))
            errors.add("must match password", User.Fields.ConfirmPassword);

        // Vérifier que le mot de passe contient au moins 8 caractères
        if (password.length() < 8)
            errors.add("minimum 8 char", User.Fields.Password);

        // Vérifier que le mot de passe contient au moins un chiffre
        if (!password.matches(".*\\d.*"))  // d signifie un chiffre, équivalent à [0-9]
            errors.add("must contain a digit", User.Fields.Password);

        // Vérifier que le mot de passe contient au moins une lettre majuscule
        if (!password.matches(".*[A-Z].*"))
            errors.add("must contain an uppercase letter", User.Fields.Password);

        // Vérifier que le mot de passe contient au moins un caractère non alphanumérique
        if (!password.matches(".*\\W.*"))
            errors.add("must contain a non-alphanumeric character", User.Fields.Password);

        return errors;
    }

}
