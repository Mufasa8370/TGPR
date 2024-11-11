package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ViewProfileController;
import tgpr.forms.model.Security;
import tgpr.framework.ViewManager;

import java.util.List;

public class ViewProfileView extends DialogWindow {
    private final ViewProfileController controller;
    private Label lblFullName;
    private Label lblEmail;
    private static ViewProfileView instanceOfView; // contient l'instance actuelle de la vue

    public ViewProfileView(ViewProfileController controller) {
        super("View Profile");
        this.controller = controller;
        instanceOfView = this;


        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(51, 8));

        var root = Panel.verticalPanel();
        setComponent(root);

        createTxtPanel().sizeTo(ViewManager.getTerminalColumns(),15).addTo(root);
        createButtonsPanel().addTo(root);
    }

    private Panel createTxtPanel() {
        var panel = Panel.verticalPanel();

        // nom d'utilisateur
        new Label("").addTo(panel); // ajout d'un espace vertical
        var welcomePanel = Panel.horizontalPanel();
        ((LinearLayout) welcomePanel.getLayoutManager()).setSpacing(0);

        new Label(" Hey ").addTo(welcomePanel);
        lblFullName = new Label(Security.getLoggedUser().getFullName())
                .setForegroundColor(TextColor.ANSI.BLUE_BRIGHT)
                .addTo(welcomePanel);
        new Label("!").addTo(welcomePanel);

        welcomePanel.addTo(panel);

        // mail
        new Label("").addTo(panel);
        var emailPanel = Panel.horizontalPanel();
        ((LinearLayout) emailPanel.getLayoutManager()).setSpacing(0);

        new Label(" I know your email address is ").addTo(emailPanel);
        lblEmail = new Label(Security.getLoggedUser().getEmail())
                .setForegroundColor(TextColor.ANSI.BLUE_BRIGHT)
                .addTo(emailPanel);
        new Label(".").addTo(emailPanel);

        emailPanel.addTo(panel);

        // "What can I do for you?"
        new Label("").addTo(panel);
        new Label(" What can I do for you?").addTo(panel);


        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = Panel.horizontalPanel().center();

        Button btnEditProfile = new Button("Edit Profile", this.controller::editProfile).addTo(panel);
        Button btnChangePassword = new Button("Change Password", this.controller::changePassword).addTo(panel);
        Button btnClose = new Button("Close", this::close).addTo(panel);

        addShortcut(btnEditProfile, KeyStroke.fromString("<A-e>"));
        addShortcut(btnChangePassword, KeyStroke.fromString("<A-e>"));
        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    public void refreshNameAndEmail() {
        lblFullName.setText(Security.getLoggedUser().getFullName());
        lblEmail.setText(Security.getLoggedUser().getEmail());
    }

    public static ViewProfileView getInstance() {
        return instanceOfView;
    }

}
