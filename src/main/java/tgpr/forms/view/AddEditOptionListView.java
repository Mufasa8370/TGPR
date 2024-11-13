package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.controller.ManageOptionListsController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.List;

import static org.mariadb.jdbc.pool.Pools.close;
import static tgpr.forms.model.Security.getLoggedUser;
import static tgpr.forms.model.Security.isAdmin;
import static tgpr.framework.Controller.askConfirmation;


public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final Panel panelForOptionListUpdate = new Panel();
    private OptionList optionList;
    private ObjectTable<OptionValue> tblOfValues;
    private List<OptionValue> listOfOptionValues;

    private List<OptionValue> listOfAddedOptionValues;

    private final Button close;
    private Button add;
    private Button duplicate;
    private Button save;
    private Button reorder;
    private Button delete;

    private Button create;


    // Attributs de classe pour les composants
    private final Panel root;

    private TextBox txtName;
    private TextBox txtNewValue;
    private CheckBox checkBoxSystem;
    private boolean checkBoxSystemIsChanged = false;

    private Panel buttonsPanel;
    private ManageOptionListsView viewManage;

    /*
    à ajouter : se mettre sur une valeur OptionValue et appuyer delete, pour supprimer la valeur. Les id changent avec
                quand checkBoxSystemIsChanged est modifié
     */


    // Edit Option List
    public AddEditOptionListView(AddEditOptionListController controller, OptionList optionList,ManageOptionListsView manageOptionListsView) {
        //Titre de la fenêtre
        super("Update Option List");
        this.viewManage = manageOptionListsView;
        this.controller = controller;
        this.optionList = optionList;
        this.listOfOptionValues = optionList.getOptionValues();
        this.listOfAddedOptionValues = new ArrayList<OptionValue>();
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);


        root = new Panel();

        // Name + TextBox

        Label nameLabel = new Label("Name:");
        root.addComponent(nameLabel);


        txtName = new TextBox();
        txtName.setText(optionList.getName());

        root.addComponent(txtName);
        root.addComponent(new EmptySpace());


        // Admin :
        if (isAdmin()) {
            Panel systemPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            Label systemLabel = new Label("System:");
            checkBoxSystem = new CheckBox();
            //addKeyboardListener(checkSystem,this::check);
            if (optionList.getOwner().isAdmin()){
                checkBoxSystem.setChecked(true);
            }

            //Button check = new Button("[ ]", this::check);

            systemPanel.addComponent(systemLabel);
            systemPanel.addComponent(checkBoxSystem);

            /*
            checkBoxSystem.addListener((boolean checked) -> {
                if (checked) {
                    optionList.setOwnerId(null); // à modifier
                } else {
                    optionList.setOwnerId(getLoggedUser().getId());
                }
            });

             */

            root.addComponent(systemPanel);
            root.addComponent(new EmptySpace());
        }

        // Tableau de values

        root.addComponent(tableOfValues(optionList));
        root.addComponent(new EmptySpace());
        setComponent(root);


        if (canModify(optionList)) {

            // TextBox + bouton add
            Panel addValue = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            txtNewValue = new TextBox();

            add = new Button("Add", this::add);
            addValue.addComponent(txtNewValue);
            addValue.addComponent(add);
            root.addComponent(addValue);
            root.addComponent(new EmptySpace());
        }

        // Buttons

        buttonsPanel = createButtonsPanelForUnusedOptionListForOwner();
        if (listOfAddedOptionValues.size() == 0) {
            duplicate = new Button("Duplicate", this::duplicate).addTo(buttonsPanel);
            buttonsPanel.addComponent(duplicate);
        }

        close = new Button("Close", this::closeForView).addTo(buttonsPanel);
        buttonsPanel.addComponent(close);

        root.addComponent(buttonsPanel);

    }

    private boolean canModify(OptionList optionList) {
        return (optionList.getOwner().equals(getLoggedUser()) || getLoggedUser().isAdmin()) && !optionList.isUsed();
    }

    private Panel createButtonsPanelForUnusedOptionListForOwner() {
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        if (canModify(optionList)) {
            reorder = new Button("Reorder", this::reorder).addTo(buttons);
            buttons.addComponent(reorder);

            delete = new Button("Delete", this::delete).addTo(buttons);
            save = new Button("Save", this::save).addTo(buttons);

            buttons.addComponent(delete);
            buttons.addComponent(save);
        }


        return buttons;
    }

    private Panel tableOfValues(OptionList optionList){
        tblOfValues = new ObjectTable<OptionValue>(
                new ColumnSpec<>("Index", OptionValue::getIdx),
                new ColumnSpec<>("Label", OptionValue::getLabel)
        );

        tblOfValues.add(listOfOptionValues);

        return Panel.gridPanel(1, Margin.of(1)).addComponent(tblOfValues);
    }


    // Add Option List
    public AddEditOptionListView(AddEditOptionListController controller) {
        //Titre de la fenêtre
        super( "Create Option List");
        this.controller = controller;
        this.optionList = new OptionList();
        this.listOfOptionValues = new ArrayList<OptionValue>();
        this.listOfAddedOptionValues = new ArrayList<OptionValue>();


        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 20 lignes et 20 colonnes
        setFixedSize(new TerminalSize(60, 15));

        root = new Panel();

        // Name + TextBox

        Label nameLabel = new Label("Name:");
        root.addComponent(nameLabel);


        txtName = new TextBox();

        root.addComponent(txtName);
        //if (txtName == null){"name required"}
        root.addComponent(new EmptySpace());

        // Tableau de values

        root.addComponent(tableOfValues(optionList));
        root.addComponent(new EmptySpace());



        // TextBox + bouton add
        Panel addValue = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        //if (listOfAddedOptionValues.isEmpty()){"at least one value required"}

        txtNewValue = new TextBox();

        add = new Button("Add", this::add);
        addValue.addComponent(txtNewValue);
        addValue.addComponent(add);
        root.addComponent(addValue);
        root.addComponent(new EmptySpace());

        // Buttons

        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        create = new Button("Create", this::create).addTo(buttonsPanel);
        close = new Button("Close", this::closeForView).addTo(buttonsPanel);

        buttonsPanel.addComponent(close);
        buttonsPanel.addComponent(create);

        root.addComponent(buttonsPanel);
        setComponent(root);
    }

    public void create(){
        if (txtName.getText() != null && !listOfAddedOptionValues.isEmpty()) {
            //this.optionList.setName(txtName.getText());
            //this.optionList.setOwnerId(getLoggedUser().getId());
            controller.save(optionList,listOfAddedOptionValues,txtName.getText(),true);
        }
    }


    public void duplicate(){
        controller.duplicate(this.optionList,this);
    }

    public void delete(){
        viewManage.close();
        controller.delete(this.optionList,this);
    }

    public void save(){
        controller.save(this.optionList,listOfAddedOptionValues,txtName.getText(),checkBoxSystem.isChecked());
        listOfAddedOptionValues.clear();
        close();
        Controller.navigateTo(new ManageOptionListsController());
    }

    public void add(){
        OptionValue newValue = new OptionValue(this.optionList,this.optionList.getNumberOfValues() + 1,txtNewValue.getText());
        listOfOptionValues.add(newValue);
        listOfAddedOptionValues.add(newValue);
        if (optionList.getNumberOfValues() == 1){
            buttonsPanel.removeComponent(duplicate);
        }
        refresh();
    }

    public void closeForView(){
        if (!listOfAddedOptionValues.isEmpty() || !txtName.getText().equals(optionList.getName())) {
            //if ((getLoggedUser().isAdmin() && checkBoxSystemisChanged) || !getLoggedUser().isAdmin()) {}
            boolean confirmed = askConfirmation("Are you sure you want to cancel?", "Cancel");
            if (confirmed) {
                listOfAddedOptionValues.clear();
                close();
                Controller.navigateTo(new ManageOptionListsController());
            }
        }
        else {
            listOfAddedOptionValues.clear();
            close();
            Controller.navigateTo(new ManageOptionListsController());
        }
    }

    public void reorder(){
        //Panel reorderButtons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        buttonsPanel.removeAllComponents();

        Button alphabetically = new Button("Alphabetcally", this::orderAlphabetically).addTo(buttonsPanel);
        Button confirmOrder = new Button("Confirm Order", this::confirmOrder).addTo(buttonsPanel);
        Button cancel = new Button("Cancel", this::cancel).addTo(buttonsPanel);

        buttonsPanel.addComponent(alphabetically);
        buttonsPanel.addComponent(confirmOrder);
        buttonsPanel.addComponent(cancel);

        root.addComponent(buttonsPanel);

        controller.reorder(this.optionList);
    }
    public void orderAlphabetically(){
        controller.orderAlphabetically(listOfOptionValues);
    }
    public void confirmOrder(){
        // setValues(liste ordonnée)
        // reorderValues(liste ordonnée)
    }
    public void cancel(){
        // change buttonsPanel
        listOfOptionValues.clear();
        listOfOptionValues = optionList.getOptionValues();
    }



    public void refresh(){
        tblOfValues.clear();
        tblOfValues.add(listOfOptionValues);
    }



}
