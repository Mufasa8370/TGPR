package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ChangePasswordController;
import tgpr.forms.model.User;

import java.util.List;

public class ChangePasswordView extends DialogWindow {
    private final ChangePasswordController controller;

    private final TextBox txtOldPassword = new TextBox();
    private final TextBox txtPassword = new TextBox();
    private final TextBox txtConfirmPassword = new TextBox();

    private final Label errOldPassword = new Label("");
    private final Label errPassword = new Label(""); //
    private final Label errConfirmPassword = new Label("");

    private Button btnSave;

    public ChangePasswordView(ChangePasswordController controller) {
        super("Change Password");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(40, 7));

        var root = Panel.verticalPanel();
        setComponent(root);

        createFieldsPanel().addTo(root);
        createButtonsPanel().addTo(root);

        txtOldPassword.takeFocus();
    }

    private Panel createFieldsPanel() {
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Old Password:").addTo(panel);
        txtOldPassword.addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errOldPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("New Password:").addTo(panel);
        txtPassword.addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Confirm Password:").addTo(panel);
        txtConfirmPassword.addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errConfirmPassword.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = Panel.horizontalPanel(1).center();
        btnSave = new Button("Save", this::save).addTo(panel).setEnabled(false);

        Button btnCancel = new Button("Cancel", this::close).addTo(panel);

        addShortcut(btnSave, KeyStroke.fromString("<A-s>"));
        addShortcut(btnCancel, KeyStroke.fromString("<A-c>"));


        return panel;
    }

    private void save() {
        controller.save(
                txtOldPassword.getText(),
                txtPassword.getText(),
                txtConfirmPassword.getText()
        );
    }

    private void validate() {
        var errors = controller.validate(
                txtOldPassword.getText(),
                txtPassword.getText(),
                txtConfirmPassword.getText()
        );

        errOldPassword.setText(errors.getFirstErrorMessage(User.Fields.Email));
        errPassword.setText(errors.getFirstErrorMessage(User.Fields.FullName));
        errConfirmPassword.setText(errors.getFirstErrorMessage(User.Fields.Password));

        btnSave.setEnabled(errors.isEmpty());
    }

}
