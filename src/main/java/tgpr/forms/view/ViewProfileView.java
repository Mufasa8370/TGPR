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

    public ViewProfileView(ViewProfileController controller) {
        super("View Profile");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(51, 8));

        var root = Panel.verticalPanel();
        setComponent(root);

        createTxtPanel().addTo(root).sizeTo(ViewManager.getTerminalColumns(),15).addTo(root);
        createButtonsPanel().addTo(root);
    }

    private Panel createTxtPanel() {
        // Création du panel principal
        var panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // nom d'utilisateur
        panel.addComponent(new Label("")); // ajout d'un espace

        var welcomePanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        ((LinearLayout) welcomePanel.getLayoutManager()).setSpacing(0);
        welcomePanel.addComponent(new Label(" Hey "));
        welcomePanel.addComponent(new Label(Security.getLoggedUser().getFullName())
                .setForegroundColor(TextColor.ANSI.BLUE_BRIGHT));
        welcomePanel.addComponent(new Label("!"));
        panel.addComponent(welcomePanel);

        panel.addComponent(new Label("")); // ajout d'un espace

        // mail
        var emailPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        ((LinearLayout) emailPanel.getLayoutManager()).setSpacing(0);
        emailPanel.addComponent(new Label(" I know your email address is "));
        emailPanel.addComponent(new Label(Security.getLoggedUser().getEmail())
                .setForegroundColor(TextColor.ANSI.BLUE_BRIGHT));
        emailPanel.addComponent(new Label("."));
        panel.addComponent(emailPanel);

        panel.addComponent(new Label("")); // ajout d'un espace


        // "What can I do for you?"
        var questionLabel = new Label(" What can I do for you?");
        panel.addComponent(questionLabel);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        Button btnEditProfile = new Button("Edit Profile").addTo(panel);
        Button btnChangePassword = new Button("Change Password").addTo(panel);
        Button btnClose = new Button("Close", this::close).addTo(panel);

        addShortcut(btnEditProfile, KeyStroke.fromString("<A-e>"));
        addShortcut(btnChangePassword, KeyStroke.fromString("<A-e>"));
        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

}
