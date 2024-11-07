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

    // meme si il y n'y a qu'un élément je dois le mettre en enum fields
    // car les fields sont utilisé dans la méthode validate qui utilise
    // la méthode add (qui doit prendre un field dans son deuxième paramètre)
    public enum Fields {
        PasswordConfirm
    }

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

        if (fullName.length() < 3)
            errors.add("minimum 3 char", User.Fields.FullName);

        if (emailExists(email))
            errors.add("already exists", User.Fields.Email);

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            errors.add("invalid email", User.Fields.Email);

        if (!password.equals(confirmPassword))
            errors.add("must match password", Fields.PasswordConfirm);

        if (password.length() < 8)
            errors.add("minimum 8 char", User.Fields.Password);

        if (!password.matches(".*\\d.*"))  // \\d siginifie un chiffre. equivalent à [0-9]
            errors.add("must contain a digit", User.Fields.Password);

        if (!password.matches(".*[A-Z].*"))
            errors.add("must contain an uppercase letter", User.Fields.Password);

        if (!password.matches(".*\\W.*"))
            errors.add("must contain a non-alphanumeric character", User.Fields.Password);

        return errors;
    }

}
