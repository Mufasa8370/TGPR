package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;
import tgpr.forms.model.UserFormAccess;
import tgpr.framework.Controller;
import tgpr.framework.ViewManager;

import java.util.List;

import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class ManageSharesView  extends DialogWindow {
    private final ManageSharesController controller;
    private final ObjectTable<User> table;
    private final User user;
    public ManageSharesView(ManageSharesController controller, User user) {
        super("Manage Shares");
        this.controller = controller;
        this.user = user;

        setHints(List.of(Hint.CENTERED));
        setFixedSize(new TerminalSize(66, 13));
        setCloseWindowWithEscape(true);
        Panel root = new Panel();
        setComponent(root);
        new EmptySpace().addTo(root);
        Panel content = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.VERTICAL));
        table = new ObjectTable<>(
                new ColumnSpec<>("Beneficiary",null),
                new ColumnSpec<>("Type",null),
                new ColumnSpec<User>("Acces right",null)
        );

        content.addComponent(table);
        // table.setMinWidth(11);

        new EmptySpace().addTo(root);
    }

}
