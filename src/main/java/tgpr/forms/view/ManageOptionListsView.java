package tgpr.forms.view;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import tgpr.forms.controller.ManageOptionListsController;

public class ManageOptionListsView {
    private final ManageOptionListsController controller;
    private final Panel panelForOptionListManagement = new Panel();
    private Button close;
    private Button newList;

    public ManageOptionListsView(ManageOptionListsController controller) {
        this.controller = controller;
    }
}
