package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.EditProfileController;
import tgpr.forms.model.User;
import java.util.List;
import static tgpr.forms.model.Security.getLoggedUser;

public class EditProfileView extends DialogWindow {
    private final EditProfileController controller;

    private final TextBox txtMail = new TextBox();
    private final TextBox txtFullName = new TextBox();

    private final Label errMail = new Label("");
    private final Label errFullName = new Label("");

    private Button btnSave;

    public EditProfileView(EditProfileController controller) {
        super("Edit Profile");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(40, 7));

        var root = Panel.verticalPanel();
        setComponent(root);

        createFieldsPanel().addTo(root);
        createButtonsPanel().addTo(root);

        txtMail.takeFocus();
    }

    private Panel createFieldsPanel() {
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Mail:").addTo(panel);
        txtMail.setText(getLoggedUser().getEmail());
        txtMail.addTo(panel)
                .sizeTo(23)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errMail.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        new Label("Full Name:").addTo(panel);
        txtFullName.setText(getLoggedUser().getFullName());
        txtFullName.addTo(panel).takeFocus()
                .sizeTo(34)
                .setTextChangeListener((txt, byUser) -> validate());
        panel.addEmpty();
        errFullName.addTo(panel).setForegroundColor(TextColor.ANSI.RED);

        return panel;
    }

    private Panel createButtonsPanel() {
        Panel panel = Panel.horizontalPanel(1).center();

        btnSave = new Button("Save", this::saveProfile).addTo(panel).setEnabled(false);
        Button btnClose = new Button("Close", this::close).addTo(panel);

        addShortcut(btnSave, KeyStroke.fromString("<A-s>"));
        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    private void validate() {
        var errors = controller.validate(
                txtMail.getText(),
                txtFullName.getText()
        );

        errMail.setText(errors.getFirstErrorMessage(User.Fields.Email));
        errFullName.setText(errors.getFirstErrorMessage(User.Fields.FullName));

        btnSave.setEnabled(errors.isEmpty());
    }

    private void saveProfile() {
        controller.save(txtMail.getText(), txtFullName.getText());
    }
}
