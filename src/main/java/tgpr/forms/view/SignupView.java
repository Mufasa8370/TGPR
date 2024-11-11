package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.SignupController;
import tgpr.forms.model.User;
import java.util.List;


public class SignupView extends DialogWindow {
    private final SignupController controller;
    private final TextBox txtMail = new TextBox();
    private final TextBox txtFullName = new TextBox();
    private final TextBox txtPassword = new TextBox();
    private final TextBox txtConfirmPassword = new TextBox();
    private final Label errMail = new Label("");
    private final Label errFullName = new Label("");
    private final Label errPassword = new Label("");
    private final Label errConfirmPassword = new Label("");
    private Button btnSignup;

    public SignupView(SignupController controller){
        super("Sign Up");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE)); // centre la fenetre et la fenetre doit avoir une taille fixe
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(54, 11));

        var root = Panel.verticalPanel(); // création d'un panel vertical appelé root
        setComponent(root); // on ajoute root à la fenetre

        createFieldsPanel().addTo(root);
        createButtonsPanel().addTo(root);

        txtMail.takeFocus();
    }

    private Panel createFieldsPanel() {
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Mail:").addTo(panel);
        txtMail.addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errMail.addTo(panel).setForegroundColor(TextColor.ANSI.RED); // label optionnel qui affiche les erreurs

        new Label("Full Name:").addTo(panel);
        txtFullName.addTo(panel).takeFocus()
                .sizeTo(34)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errFullName.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Password:").addTo(panel);
        txtPassword.setMask('*').addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Confirm Password:").addTo(panel);
        txtConfirmPassword.setMask('*').addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errConfirmPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();
        btnSignup = new Button("Signup", this::signup).addTo(panel).setEnabled(false);

        Button btnClose = new Button("Close", this::close).addTo(panel);

        addShortcut(btnSignup, KeyStroke.fromString("<A-s>"));
        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    private void signup() {
        controller.save(
                txtMail.getText(),
                txtFullName.getText(),
                txtPassword.getText(),
                txtConfirmPassword.getText()
        );
    }

    private void validate() {
        var errors = controller.validate(
                txtMail.getText(),
                txtFullName.getText(),
                txtPassword.getText(),
                txtConfirmPassword.getText()
        );
        errMail.setText(errors.getFirstErrorMessage(User.Fields.Email));
        errFullName.setText(errors.getFirstErrorMessage(User.Fields.FullName));
        errPassword.setText(errors.getFirstErrorMessage(User.Fields.Password));
        errConfirmPassword.setText(errors.getFirstErrorMessage(SignupController.Fields.PasswordConfirm));

        btnSignup.setEnabled(errors.isEmpty());
    }

}
