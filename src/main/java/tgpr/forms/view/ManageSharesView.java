package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.EditConfirmationSharesController;
import tgpr.forms.controller.ManageSharesController;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.DistListFormAccess;
import tgpr.forms.model.UserFormAccess;
import tgpr.framework.Model;
import tgpr.forms.model.Form;
import java.util.List;

import static tgpr.framework.Controller.navigateTo;
import static tgpr.framework.Tools.asString;

public class ManageSharesView  extends DialogWindow {
    private final ManageSharesController controller;
    private final ObjectTable<Model> table;
    private final Button btnAddFilter;
    private final Button btnClose;
    private final AutoCompleteComboBox<Model> addFilter;
    private final Form form;
    public ManageSharesView(ManageSharesController controller,Form form) {
        super("Manage Shares");
        this.controller = controller;
        this.form = form;
        setHints(List.of(Hint.CENTERED));
        setFixedSize(new TerminalSize(66, 10));
        Panel root = new Panel();
        setComponent(root);
        new EmptySpace().addTo(root);
        Panel content = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.VERTICAL));
//
//        var bColumn = new ColumnSpec<>("Beneficiary",ManageSharesController::Abeneficiary);
//        bColumn.setWidth(22);
//
//        var tColumn = new ColumnSpec<ManageSharesController>("Type",ManageSharesController::Abeneficiary);
//        tColumn.setWidth(22);
//
//        var aColumn = new ColumnSpec<ManageSharesController>("Access right",ManageSharesController::Abeneficiary);
//
//        table = new ObjectTable<>(
//                bColumn,
//                tColumn,
//                aColumn
//        );

        table = new ObjectTable<>(
                // m est une instance de classe Member.
                // va parcourir le table de la base de donnée ligne par ligne
                // et va afficher les données dans membres en fonctions des spécificités.
                new ColumnSpec<Model>("Beneficiary",(m)->{
                    if(m instanceof UserFormAccess){
                        return ((UserFormAccess) m).getUser().getFullName();
                    }
                    else if(m instanceof DistListFormAccess){
                        return ((DistListFormAccess) m).getDistList().getName();
                    }
                    else{
                        return null;
                    }
                }),
                new ColumnSpec<>("Type",(m)->{
                    if(m instanceof UserFormAccess){
                        return "User";
                    }
                    else if(m instanceof DistListFormAccess){
                        return "Distribution List";
                    }
                    else{
                        return null;
                    }
                }),
                new ColumnSpec<>("Access right", (m)->{
                    if(m instanceof UserFormAccess){
                        return  ((UserFormAccess) m).getAccessType();
                    }
                    else if(m instanceof DistListFormAccess){
                        return ((DistListFormAccess) m).getAccessType();
                    }
                    else{
                        return null;
                    }
                })
        );
        addKeyboardListener(
                table, (KeyStroke stroke)->{
                    return this.handleWeightKeyStroke(stroke);

                }
        );

        content.addComponent(table);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        Panel filter = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        addFilter = new AutoCompleteComboBox<>();
        addFilter.addTo(filter);
        addFilter.setPreferredSize(new TerminalSize(15, 1)).addTo(filter);

//        btnAddFilter = new Button("Add", () -> {
//            var beneficiary = addFilter.getSelectedItem();
//            var accessType = cboType.getSelectedItem();
//            controller.addAccess(beneficiary, accessType);
//            reloadData();
//        }).addTo(filter);

        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        Panel closePanel = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        btnClose = new Button("Close", () -> {
            this.close();
        }).addTo(closePanel);
        root.addComponent(btnClose, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        reloadData();
        addFilter.takeFocus();
    }

    public void reloadData(){
        table.clear();
        addFilter.clearItems();
        addFilter.addItems(controller.getPotentialBeneficiaries());
        table.add(controller.getAccesses());
    }

    public boolean handleWeightKeyStroke(KeyStroke keyStroke){
        KeyType type = keyStroke.getKeyType();
        // Backspace -> touch delete
        // Enter -> touch enter
        if(type ==KeyType.Backspace || (type == KeyType.Enter && form.getIsPublic() == false)){
            controller.gotoEditConfirmationShares(table.getSelected(),type);
            reloadData();
        }
        return true;
    }
}

