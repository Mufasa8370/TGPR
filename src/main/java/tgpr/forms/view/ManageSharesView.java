package tgpr.forms.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;
import tgpr.framework.Controller;

public class ManageSharesView  extends DialogWindow {
    private final ManageSharesController controller;
    private final User user;
    public ManageSharesView(ManageSharesController controller, User user) {
        super("Manage Shares");
        this.controller = controller;
        this.user = user;


    }
}
