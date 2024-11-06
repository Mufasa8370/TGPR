package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.Form;
import tgpr.forms.model.UserFormAccess;
import tgpr.framework.Controller;
import tgpr.framework.ViewManager;

import java.util.List;

import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class ManageSharesView  extends DialogWindow {
    private final ManageSharesController controller;
    private final ObjectTable<Object> table;
    public ManageSharesView(ManageSharesController controller) {
        super("Manage Shares");
        this.controller = controller;

        setHints(List.of(Hint.CENTERED));
        setFixedSize(new TerminalSize(66, 13));
        setCloseWindowWithEscape(true);
        Panel root = new Panel();
        setComponent(root);
        new EmptySpace().addTo(root);
        Panel content = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.VERTICAL));

        var bColumn = new ColumnSpec<>("Beneficiary",null);
        bColumn.setWidth(22);

        var tColumn = new ColumnSpec<>("Type",null);
        tColumn.setWidth(22);

        var aColumn = new ColumnSpec<>("Access right",null);

        table = new ObjectTable<>(
                bColumn,
                tColumn,
                aColumn
        );

        content.addComponent(table);
        // table.setMinWidth(11);

        new EmptySpace().addTo(root);
    }

}
