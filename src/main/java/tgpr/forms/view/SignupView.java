package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.SignupController;
import tgpr.framework.ViewManager;

import java.util.List;

public class SignupView extends DialogWindow {
    private final SignupController controller;
    private final TextBox txtMail = new TextBox();
    private final TextBox txtFullName = new TextBox();
    private final TextBox txtPassword = new TextBox();
    private final TextBox txtConfirmPassword = new TextBox();

    private Button btnSignup;

    public SignupView(SignupController controller){
        super("Sign Up");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setFixedSize(new TerminalSize(54, 11));

        var root = Panel.verticalPanel(1);
        setComponent(root);

        createFieldsPanel().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning)).sizeTo(ViewManager.getTerminalColumns(),15).addTo(root);
        createButtonsPanel().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center)).addTo(root);

        btnSignup.takeFocus();
    }

    private Panel createFieldsPanel() {
        Panel panel = Panel.gridPanel(2, Margin.of(1, 1, 1, 0), Spacing.of(1)).center();

        new Label("Mail:").addTo(panel);
        txtMail.addTo(panel).takeFocus();
        txtMail.setPreferredSize(new TerminalSize(23, 1));

        new Label("Full Name:").addTo(panel);
        txtFullName.setMask('*').addTo(panel);
        txtFullName.setPreferredSize(new TerminalSize(34, 1));

        new Label("Password:").addTo(panel);
        txtPassword.setMask('*').addTo(panel);
        txtPassword.setPreferredSize(new TerminalSize(23, 1));

        new Label("Confirm Password:").addTo(panel);
        txtConfirmPassword.setMask('*').addTo(panel);
        txtConfirmPassword.setPreferredSize(new TerminalSize(23, 1));

        return panel;
    }

    private Panel createButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();
        btnSignup = new Button("Signup", this::signup).addTo(panel);

        Button btnClose = new Button("Close", this::close).addTo(panel);


        addShortcut(btnSignup, KeyStroke.fromString("<A-s>"));
        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    private void signup() {
        //controller.signup(txtMail.getText(), txtFullName.getText(), txtPassword.getText(), txtConfirmPassword.getText());
    }


}
