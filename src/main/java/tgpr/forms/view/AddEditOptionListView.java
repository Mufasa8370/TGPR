package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.controller.ManageOptionListsController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.model.User;
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

/*
    private final Button reorder;
    private final Button delete;
    private final Button duplicate;
    private final Button save;

 */
    private final Button close;
    private final Button add;


    // Attributs de classe pour les composants
    private final Panel root;
    private Panel titlePanel;

    private TextBox txtName;
    private TextBox txtNewValue;
    //private Panel optionValuePanel;

    private Panel buttonsPanel;


    // Edit Option List
    public AddEditOptionListView(AddEditOptionListController controller, OptionList optionList) {
        //Titre de la fenêtre
        super("Update Option List");
        this.controller = controller;
        this.optionList = optionList;
        this.listOfOptionValues = optionList.getOptionValues();
        this.listOfAddedOptionValues = new ArrayList<OptionValue>();
        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);

        /*
        if (user is owner) = peut modifier
            boutons = reorder, delete,save,close
         */

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
            root.addComponent(nameLabel);


            Button check = new Button("[]", this::check);


            root.addComponent(systemPanel);
            root.addComponent(new EmptySpace());
        }

        // Tableau de values

        root.addComponent(tableOfValues(optionList));
        root.addComponent(new EmptySpace());
        setComponent(root);

        // TextBox + bouton add
        Panel addValue = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        txtNewValue = new TextBox();

        add = new Button("Add", this::add);
        addValue.addComponent(txtNewValue);
        addValue.addComponent(add);
        root.addComponent(addValue);
        root.addComponent(new EmptySpace());

        // Buttons

        buttonsPanel = createButtonsPanelForUnusedOptionListForOwner();
        Button duplicate = new Button("Duplicate", this::duplicate).addTo(buttonsPanel);
        close = new Button("Close", this::closeForView).addTo(buttonsPanel);

        buttonsPanel.addComponent(close);
        buttonsPanel.addComponent(duplicate);

        root.addComponent(buttonsPanel);

    }

    private Panel createButtonsPanelForUnusedOptionListForOwner() {
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        User u = getLoggedUser();

        if (u.equals(optionList.getOwner()) && !optionList.isUsed()) {

            Button reorder = new Button("Reorder", this::reorder).addTo(buttons);
            Button delete = new Button("Delete", this::delete).addTo(buttons);
            Button save = new Button("Save", this::save).addTo(buttons);

            buttons.addComponent(reorder);
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
        //this.optionList = optionList;
        this.listOfAddedOptionValues = new ArrayList<OptionValue>();


        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 20 lignes et 20 colonnes
        setFixedSize(new TerminalSize(20, 20));

        root = new Panel();

        // Name + TextBox

        //new Label("Name:").addTo(root);
        Label nameLabel = new Label("Name:");
        root.addComponent(nameLabel);

        if (optionList != null) {
            txtName = new TextBox(optionList.getName());
        }
        else {
            txtName = new TextBox();
        }
        root.addComponent(new EmptySpace());



        // TextBox + bouton add
        txtNewValue = new TextBox();
        add = new Button("Add", this::add).addTo(root);
        root.addComponent(new EmptySpace());

        // Buttons

        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        Button duplicate = new Button("Duplicate", this::duplicate).addTo(buttonsPanel);
        close = new Button("Close", this::closeForView).addTo(buttonsPanel);

        buttonsPanel.addComponent(close);
        buttonsPanel.addComponent(duplicate);

        root.addComponent(buttonsPanel);

    }

    public void duplicate(){
        controller.duplicate(this.optionList);
    }

    public void delete(){
        controller.delete(this.optionList);
    }

    public void save(){
        controller.addForSave(this.optionList,listOfAddedOptionValues);
        listOfAddedOptionValues.clear();
        close();
        Controller.navigateTo(new ManageOptionListsController());
    }

    public void reorder(){
        controller.reorder(this.optionList);
    }

    public void add(){
        OptionValue newValue = new OptionValue(this.optionList,this.optionList.getNumberOfValues() + 1,txtNewValue.getText());
        listOfOptionValues.add(newValue);
        listOfAddedOptionValues.add(newValue);
        refresh();
    }

    public void closeForView(){
        if (!listOfAddedOptionValues.isEmpty()) {
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

    public void refresh(){
        tblOfValues.clear();
        tblOfValues.add(listOfOptionValues);
    }

    public void check(){}




}
