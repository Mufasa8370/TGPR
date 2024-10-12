package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.framework.Configuration;
import tgpr.forms.controller.LoginController;

import java.util.List;

import static tgpr.framework.Controller.showError;

public class LoginView extends BasicWindow {

    private final LoginController controller;
    private final TextBox txtEmail;
    private final TextBox txtPassword;
    private final Button btnLogin, btnSignup, btnExit, btnLoginAsGuest;

    public LoginView(LoginController controller) {
        this.controller = controller;

        setTitle("Login");
        setHints(List.of(Hint.CENTERED));

        Panel root = new Panel();
        setComponent(root);

        Panel panel = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1).setVerticalSpacing(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        panel.addComponent(new Label("Mail:"));
        txtEmail = new TextBox().addTo(panel);
        panel.addComponent(new Label("Password:"));
        txtPassword = new TextBox().setMask('*').addTo(panel);

        new EmptySpace().addTo(root);

        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        btnLogin = new Button("Login", this::login).addTo(buttons);
        btnSignup = new Button("Signup", this::signup).addTo(buttons);
        btnExit = new Button("Exit", this::exit).addTo(buttons);
        Panel buttonLog = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        //Le Guest ne peut rien faire appart login et signup, du coup que dois faire ce boutton ?
        btnLoginAsGuest = new Button("Login as guest", this::logAsGuest).addTo(buttonLog);
        new EmptySpace().addTo(root);

        Button btnSeedData = new Button("Reset Database", this::seedData);
        Panel debug = Panel.verticalPanel(LinearLayout.Alignment.Center,
                new Button("Login as default admin", this::logAsDefaultAdmin),
                new Button("Login as bepenelle@epfc.eu", this::logAsBePenelle),
                //Je dois rajouter ca dans la config mais cela va modifier le tronc commun
                new Button("Login as mamichel@epfc.eu", this::logAsMaMichel),
                new Button("Login as xapigeolet@epfc.eu", this::logAsXavierPigeolet),
                btnSeedData
        );
        debug.withBorder(Borders.singleLine(" For debug purpose ")).addTo(root);

        txtEmail.takeFocus();
    }

    private void logAsGuest() {
        showError(String.valueOf(new Error("In Progress : Le Guest ne peut rien faire appart login et signup, du coup que dois faire ce boutton ?")));
    }

    private void logAsXavierPigeolet() {
        controller.login(Configuration.get("default.user.mail.xavier"), Configuration.get("default.user.password"));
    }

    private void logAsMaMichel() {
        controller.login(Configuration.get("default.user.mail.mamichel"), Configuration.get("default.user.password"));
    }

    private void seedData() {
        controller.seedData();
        btnLogin.takeFocus();
    }

    private void exit() {
        controller.exit();
    }

    private void login() {
        var errors = controller.login(txtEmail.getText(), txtPassword.getText());
        if (!errors) {
            txtEmail.takeFocus();
        }
    }
///////////////////////////////////////////
    private void signup() {
        controller.signup();
    }

    private void logAsDefaultAdmin() {
        controller.login(Configuration.get("default.admin.mail"), Configuration.get("default.admin.password"));
    }

    private void logAsBePenelle() {
        controller.login(Configuration.get("default.user.mail"), Configuration.get("default.user.password"));
    }
}