package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.LoginController;
import tgpr.forms.controller.ManageDistListsController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.*;

import java.util.ArrayList;
import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;
import static tgpr.framework.Controller.askConfirmation;
import static tgpr.framework.Controller.navigateTo;

public class ManageDistListsView extends DialogWindow {
    private final ManageDistListsController controller;
    private Panel panelForCombo = new Panel();
    private Panel root = new Panel();
    private Panel panelForTable = new Panel();
    private Panel panelForUserInList= new Panel();
    private Panel buttons= new Panel();
    private Button close = new Button("Close",this::close);
    private Button delete = new Button("Delete",this::delete);

    private void delete() {
        boolean confirmed = askConfirmation("Are you sure you want to delete this distribution list?", "Delete");
        if (confirmed) {

            current.deleteAllFormAccesses();
            current.deleteAllUsers();
            current.delete();
            close();
            navigateTo(new ManageDistListsController());

        }

    }

    private Button cancel = new Button("Cancel",this::cancel);

    private void cancel() {
        close();
        navigateTo(new ManageDistListsController());

    }

    private Button save = new Button("Save",this::save);

    private void save() {
        if (current != null) {
            current.deleteAllUsers();
            for(User user : usersInList){
                DistListUser distListUser = new DistListUser(current,user);
                distListUser.save();
            }
        }else {
            System.out.println("null");
            DistList distList  = new DistList(nameCurrentDst);
            distList.setOwnerId(getLoggedUser().getId());
            distList.save();
            for(User user : usersInList){
                DistListUser distListUser = new DistListUser(distList,user);
                distListUser.save();
            }
        }
        close();
        navigateTo(new ManageDistListsController());

    }

    private Panel panelForUserOther= new Panel();
    private ObjectTable<User> userOtherTable;
    private ObjectTable<User> userInTable;
    List<DistList> distLists = DistList.getAll();
    DistList current = null;
    private AutoCompleteComboBox autoCompleteComboBox;
    List<User> usersInList = new ArrayList<>();
    private Label noItemInList = new Label("at least one user required").setForegroundColor(TextColor.ANSI.RED).setVisible(false);
    private Label goodLabel =new Label("(*)").setVisible(false);
    private boolean listExist = false;
    private String nameCurrentDst = "";


    public ManageDistListsView(ManageDistListsController controller) {
        super("Distribution List Management");
        this.controller = controller;
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE)); // centre la fenetre et la fenetre doit avoir une taille fixe
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 20));
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);
        root.addComponent(new EmptySpace());

        root.addComponent(createViewComboBox());
        panelForTable.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        panelForTable.setPreferredSize(new TerminalSize(100,100));

        Panel userInPanel = createViewPanelUserInList();
        Panel userOtherPanel = createViewPanelUserOther();

        panelForTable.addComponent(userInPanel);
        panelForTable.addComponent(userOtherPanel);


        userInPanel.setPreferredSize(new TerminalSize(70, 15));
        userOtherPanel.setPreferredSize(new TerminalSize(70, 15));
        root.addComponent(panelForTable);

        root.addComponent(createPanelButton());
        autoCompleteComboBox.takeFocus();

    }

    public Panel createViewComboBox(){
        panelForCombo.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        panelForCombo.addComponent(new Label("List: "));
        // Avec des éléments qui sont des strings

        autoCompleteComboBox = new AutoCompleteComboBox<>("",distLists);
        autoCompleteComboBox.setPreferredSize(new TerminalSize(40,1));
        autoCompleteComboBox.addListener(new AutoCompleteComboBox.Listener() {
            @Override
            public void onSelectionChanged(int selectedIndex, int previousSelection, boolean changedByUserInteraction) {

                for (DistList ds:distLists) {
                    if (ds.getName().equals(autoCompleteComboBox.getText())){
                        System.out.println(autoCompleteComboBox.getText());
                        usersInList = ds.getUsers();
                        current = ds;

                        listExist = true;
                    }
                }
                if(!listExist){
                    usersInList.clear();
                    current = null;
                    noItemInList.setVisible(true);
                    delete.setVisible(false);
                }else {
                    noItemInList.setVisible(false);
                    delete.setVisible(true);
                }

                nameCurrentDst = autoCompleteComboBox.getText();

                reload();
                listExist = false;


            }
        });
        panelForCombo.addComponent(autoCompleteComboBox);
        panelForCombo.addComponent(goodLabel);
        return panelForCombo;
    }

    public void reload(){
        controller.listForUserInTable(userInTable,usersInList);
        controller.listForUserOtherTable(userOtherTable,usersInList);

    }
    public Panel createViewPanelUserInList(){
        var pnl = Panel.gridPanel(1, Margin.of(1));

        userInTable = new ObjectTable<>(
                new ColumnSpec<User>("User in the List", User::getFullName)
                        .setMinWidth(20).alignLeft()
        );
        userInTable.setSelectAction(() -> {
            User selectedUser = userInTable.getSelected();
            if (selectedUser != null) {
                usersInList.remove(selectedUser);
                if (usersInList.isEmpty()){
                    noItemInList.setVisible(true);
                    goodLabel.setVisible(false);
                    save.setVisible(false);
                    cancel.setVisible(false);
                    autoCompleteComboBox.setReadOnly(false);
                }else {
                    goodLabel.setVisible(true);
                    save.setVisible(true);
                    cancel.setVisible(true);
                    autoCompleteComboBox.setReadOnly(true);
                }
                delete.setVisible(false);

                reload();
            }
        });
        userInTable.setPreferredSize(new TerminalSize(50,12));


        // Ajouter le tableau au panneau
        pnl.addComponent(userInTable);
        pnl.addComponent(noItemInList);

        return pnl;
    }

    public Panel createViewPanelUserOther(){
        var pnlOther= Panel.gridPanel(1, Margin.of(1));
        userOtherTable = new ObjectTable<>(
                new ColumnSpec<User>("Other users", User::getFullName)
                        .setMinWidth(20).alignLeft()
        );

        controller.listForUserOtherTable(userOtherTable,usersInList);
        userOtherTable.setSelectAction(() -> {
            User selectedUser = userOtherTable.getSelected();
            if (selectedUser != null) {
                usersInList.add(selectedUser);
                noItemInList.setVisible(false);
                delete.setVisible(false);
                save.setVisible(true);
                cancel.setVisible(true);
                goodLabel.setVisible(true);
                autoCompleteComboBox.setReadOnly(true);

                reload();
            }
        });

        pnlOther.addComponent(userOtherTable);

        return pnlOther;
    }

    public Panel createPanelButton(){
        var panel = Panel.horizontalPanel().center();
        panel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        panel.addComponent(save.setVisible(false));
        panel.addComponent(delete.setVisible(false));
        panel.addComponent(cancel.setVisible(false));
        panel.addComponent(close);
        return panel;

    }
}
