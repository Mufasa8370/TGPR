package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
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
import static tgpr.framework.Controller.navigateTo;


public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private OptionList optionList;
    private ObjectTable<OptionValue> tblOfValues;
    private List<OptionValue> listOfOptionValues;

    private List<OptionValue> listOfAddedOptionValues;

    private final Button close;
    private Button add;
    private Button duplicate;
    private Button save;
    private Button delete;

    private Button reorder;
    private Button alphabetically;
    private Button confirmOrder;
    private Button cancel;

    private Button create;


    // Attributs de classe pour les composants
    private final Panel root;

    private TextBox txtName;
    private TextBox txtNewValue;
    private CheckBox checkBoxSystem;
    private boolean checkBoxSystemIsChanged = false;

    private Panel buttonsPanel;
    private Panel addValuePanel;
    private Panel namePanel;
    private final Label nameLabel = new Label("Name:");
    private ManageOptionListsView viewManage;

    private int compterIdx = 0;

    private final Label errNoName = new Label("name required");
    private final Label errNoValueAdded = new Label("at least one value required");


    private boolean reorderMode = false, reoderSelect = false;
    private OptionValue current = null;
    private boolean auto = false;

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

        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);


        root = new Panel();

        // Name + TextBox

        namePanel = Panel.gridPanel(2, Margin.of(1));

        namePanel.addComponent(nameLabel);


        txtName = new TextBox();
        txtName.setText(optionList.getName());
        txtName.addTo(namePanel).takeFocus().sizeTo(40);

        root.addComponent(namePanel);
        root.addComponent(new EmptySpace());


        // Admin :
        if (isAdmin()) {
            Panel systemPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            Label systemLabel = new Label("System:");
            checkBoxSystem = new CheckBox();
            if (optionList.getOwner() == null){
                checkBoxSystem.setChecked(true);
            }

            systemPanel.addComponent(systemLabel);
            systemPanel.addComponent(checkBoxSystem);


            root.addComponent(systemPanel);
            root.addComponent(new EmptySpace());
        }
        else {
            checkBoxSystem = new CheckBox();
            if (optionList.getOwner() == null){
                checkBoxSystem.setChecked(true);
            }
            root.addComponent(checkBoxSystem).setVisible(false);
        }

        // Tableau de values

        root.addComponent(tableOfValues(optionList));
        if (canModify(optionList)) {
            addKeyboardListener(
                    tblOfValues, (KeyStroke stroke) -> {
                        return this.handleWeightKeyStroke(stroke);
                    });
        }

        root.addComponent(new EmptySpace());
        setComponent(root);


        if (canModify(optionList)) {

            // TextBox + bouton add
            addValuePanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

            txtNewValue = new TextBox();

            add = new Button("Add", this::add);
            txtNewValue.addTo(addValuePanel).takeFocus().sizeTo(40);

            addValuePanel.addComponent(txtNewValue);
            addValuePanel.addComponent(add);
            root.addComponent(addValuePanel);
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
        return (optionList.getOwner() == null && getLoggedUser().isAdmin()) ||(optionList.getOwner()!= null && optionList.getOwner().equals(getLoggedUser())) || (getLoggedUser().isAdmin()) && !optionList.isUsed();
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
        namePanel = Panel.gridPanel(2, Margin.of(1));

        Label nameLabel = new Label("Name:");
        namePanel.addComponent(nameLabel);

        txtName = new TextBox();
        txtName.setTextChangeListener((newText, changedByUserInteraction) -> {
            errNoName.setVisible(false);
        });
        txtName.addTo(namePanel).takeFocus().sizeTo(40);
        errNoName.addTo(namePanel).setForegroundColor(TextColor.ANSI.RED);

        root.addComponent(namePanel);
        root.addComponent(new EmptySpace());

        // Tableau de values

        root.addComponent(tableOfValues(optionList));
        root.addComponent(new EmptySpace());

        addKeyboardListener(
                tblOfValues, (KeyStroke stroke) -> {
                    return this.handleWeightKeyStroke(stroke);
                });

        // TextBox + bouton add
        addValuePanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        addValuePanel.addComponent(new EmptySpace());
        txtNewValue = new TextBox();
        txtNewValue.addTo(addValuePanel).takeFocus().sizeTo(40);

        add = new Button("Add", this::addForCreate);
        addValuePanel.addComponent(add);

        root.addComponent(addValuePanel);
        errNoValueAdded.addTo(root).setForegroundColor(TextColor.ANSI.RED);
        root.addComponent(new EmptySpace());

        // Buttons

        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        create = new Button("Create", this::create).addTo(buttonsPanel);
        create.setEnabled(txtName.getText().isEmpty());

        close = new Button("Close", this::closeForView).addTo(buttonsPanel);

        buttonsPanel.addComponent(close);
        buttonsPanel.addComponent(create);

        root.addComponent(buttonsPanel);
        setComponent(root);
    }

    public void create(){
        if (!txtName.getText().equals("") && !listOfAddedOptionValues.isEmpty()) {
            controller.create(optionList,listOfAddedOptionValues,txtName.getText());
            listOfAddedOptionValues.clear();
            close();
            navigateTo(new ManageOptionListsController());
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
        navigateTo(new ManageOptionListsController());
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

    public void addForCreate(){
        ++compterIdx;
        OptionValue newValue = new OptionValue(this.optionList,compterIdx,txtNewValue.getText());
        listOfOptionValues.add(newValue);
        listOfAddedOptionValues.add(newValue);
        if (optionList.getNumberOfValues() == 1){
            buttonsPanel.removeComponent(duplicate);
        }
        errNoValueAdded.setVisible(false);
        refresh();
    }

    public void closeForView(){
        if (!listOfAddedOptionValues.isEmpty() || !txtName.getText().equals(optionList.getName())) {
            boolean confirmed = askConfirmation("Are you sure you want to cancel?", "Cancel");
            if (confirmed) {
                listOfAddedOptionValues.clear();
                close();
                navigateTo(new ManageOptionListsController());
            }
        }
        else {
            listOfAddedOptionValues.clear();
            close();
            navigateTo(new ManageOptionListsController());
        }
    }

    public void reorder(){
        reorderMode = true;
        buttonsPanel.removeAllComponents();

        Button alphabetically = new Button("Alphabetcally", this::orderAlphabetically).addTo(buttonsPanel);
        Button confirmOrder = new Button("Confirm Order", this::confirmOrder).addTo(buttonsPanel);
        Button cancel = new Button("Cancel", this::cancel).addTo(buttonsPanel);
        tblOfValues.takeFocus();
        tblOfValues.setSelected(listOfOptionValues.getLast());

        tblOfValues.setSelectAction(() -> {
            reorderSelect();
        });

        buttonsPanel.addComponent(alphabetically);
        buttonsPanel.addComponent(confirmOrder);
        buttonsPanel.addComponent(cancel);

        root.addComponent(buttonsPanel);
    }

    public void orderAlphabetically(){
        controller.orderAlphabetically(listOfOptionValues);
        tblOfValues.clear();
        tblOfValues.add(listOfOptionValues);
    }

    public void confirmOrder(){
        optionList.reorderValues(listOfOptionValues);
        optionList.save();
        close();
        navigateTo(new AddEditOptionListController(optionList,viewManage));
    }

    public void changeOrder(List<OptionValue> values, int oldInd, int newInd){
        OptionValue oldValue = values.get(oldInd);
        OptionValue newValue = values.get(newInd);
        values.set(oldInd,newValue);
        values.set(newInd,oldValue);

    }

    private void reorderSelect() {
        System.out.println(reoderSelect);
        if(!reoderSelect){
            current = tblOfValues.getSelected();

            tblOfValues.addSelectionChangeListener(new ObjectTable.SelectionChangeListener() {
                @Override
                public void onSelectionChanged(int oldRow, int newRow, boolean byUser) {

                    if(!auto){
                        changeOrder(listOfOptionValues,oldRow,newRow);
                        tblOfValues.clear();
                        tblOfValues.add(listOfOptionValues);
                        if(newRow != 0){
                            auto = true;
                        }

                        tblOfValues.setSelected(listOfOptionValues.get(newRow));
                    }else {
                        auto = false;
                    }
                }
            });
            reoderSelect = true;
        }else {

            reoderSelect = false;
            tblOfValues.addSelectionChangeListener(new ObjectTable.SelectionChangeListener() {
                @Override
                public void onSelectionChanged(int oldRow, int newRow, boolean byUser) {

                }
            });
        }

    }


    public void cancel(){
        close();
        navigateTo(new AddEditOptionListController(optionList,viewManage));
    }

    public void refresh(){
        tblOfValues.clear();
        tblOfValues.add(listOfOptionValues);
    }

    public boolean handleWeightKeyStroke(KeyStroke keyStroke){
        KeyType type = keyStroke.getKeyType();
        if(type == KeyType.Delete || type == KeyType.Backspace){
            deleteValue();
        }
        return true;
    }

    public void deleteValue(){
        //tblOfValues.getSelected().delete();
        listOfOptionValues.remove(tblOfValues.getSelected());
        optionList.reorderValues(listOfOptionValues);
        refresh();
    }




}
