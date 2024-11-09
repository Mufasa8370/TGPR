package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.ViewInstancesController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.framework.ViewManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static tgpr.framework.Tools.asString;

public class ViewInstancesView extends DialogWindow {
    private final ViewInstancesController controller;
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private Form form;
    private ObjectTable<Instance> instancesTable;
    private Panel pnlInstances;
    private final Label lblNoInstances = new Label("No instances");

    public ViewInstancesView(ViewInstancesController controller, Form form){
        super("List of Submitted Instances");
        this.controller = controller;
        this.form = form;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 20));

        var root = Panel.verticalPanel();
        setComponent(root);

        createFields().addTo(root);
        createSubmittedInstancesPanel().sizeTo(ViewManager.getTerminalColumns(),15).addTo(root);
        createButtonsPanel().addTo(root);

        refresh();
    }

    private Panel createFields(){
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);
        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Panel createSubmittedInstancesPanel() {
        var panel = pnlInstances = Panel.gridPanel(1, Margin.of(1));

        instancesTable = new ObjectTable<>(
                new ColumnSpec<>("Id", Instance::getId)
                        .setMinWidth(4).alignRight(),
                new ColumnSpec<>("User", Instance::getUser)
                        .setMinWidth(30),
                new ColumnSpec<Instance>("Submitted", i -> asString(i.getCompleted()))
                        .setMinWidth(20)
        ).addTo(panel);

        instancesTable.setSelectAction(() -> {
            var instance = instancesTable.getSelected();
            controller.viewEditInstance(instance);
        });

        this.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
                var instance = instancesTable.getSelected();
                if (keyStroke.getKeyType() == KeyType.Backspace || keyStroke.getKeyType() == KeyType.Delete) {
                    if (instance != null) {
                        controller.deleteInstance(instance);
                        refresh();
                        hasBeenHandled.set(true);
                    }
                }
            }
        });

        instancesTable.addSelectionChangeListener(this::onInstanceSelectionChanged);

        return panel;
    }

    private void onInstanceSelectionChanged(int oldValue, int newValue, boolean byUser) {
        Instance selectedInstance = instancesTable.getSelected();
        if (selectedInstance != null) {
            controller.setSelectedInstance(selectedInstance);
        }
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        if (!controller.getSubmittedInstances().isEmpty()){
            Button btnDeleteSelected = new Button("Delete Selected", this.controller::testDelete).addTo(panel);

            Button btnDeleteAll = new Button("Delete All", this::deleteAll).addTo(panel);
        }

        Button btnClose = new Button("Close", this::close).addTo(panel);

        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    public void refresh() {
        if (form != null) {
            lblTitle.setText(form.getTitle());
            lblDescription.setText(form.getDescription());

            var instances = controller.getSubmittedInstances();
            pnlInstances.removeAllComponents();
            if (instances.isEmpty()) {
                pnlInstances.addComponent(lblNoInstances);
            } else {
                pnlInstances.addComponent(instancesTable);
                instancesTable.clear();
                instancesTable.add(instances);
            }
            onInstanceSelectionChanged(-1, instancesTable.getSelectedRow(), true);
        }
    }

    private void deleteAll() {
        controller.deleteAll();
        refresh();
    }

}
