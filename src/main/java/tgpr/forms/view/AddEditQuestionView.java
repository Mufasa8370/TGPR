package tgpr.forms.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.controller.AddEditQuestionController;
import tgpr.forms.controller.ViewFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;
import static tgpr.framework.Controller.askConfirmation;
import static tgpr.framework.Controller.showMessage;

public class AddEditQuestionView extends DialogWindow {

    private final AddEditQuestionController controller;
    ManageOptionListsView manageOptionListsView;
    private final TextBox txtTitle;
    private final TextBox txtDescription;
    private final ComboBox<Question.Type> cboType;
    private final CheckBox chkRequired;
    private final ComboBox<OptionList> cboOptionList;
    private final Button btnAdd;
    private Button btnCreate;
    private final Label errTitle;
    private final Label errDescription;
    private Label errOptionList;
    private final Question question;
    private Form form;
    private ViewFormView formView;

    public AddEditQuestionView(AddEditQuestionController controller, Question question, Form form, ViewFormView formView) {
        super(question == null ? "Add a question" : "Edit a question");

        this.formView = formView;
        this.controller = controller;
        this.question = question;
        this.form = form;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(80, 16));

        Panel root = new Panel();
        setComponent(root);
        Panel fields = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);
        new Label("Title:").addTo(fields);
        txtTitle = new TextBox(new TerminalSize(40,1))
                .addTo(fields).takeFocus();
        if (question != null) {
            txtTitle.setText(question.getTitle());  // Pré-remplir le champ titre
        }
        new EmptySpace().addTo(fields);
        errTitle = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        txtTitle.setTextChangeListener((newText, changedText) -> validate());
        new Label("Description:").addTo(fields);
        txtDescription = new TextBox(new TerminalSize(50, 3), "", TextBox.Style.MULTI_LINE).addTo(fields);
        if (question != null) {
            txtDescription.setText(question.getDescription());  // Pré-remplir le champ titre
        }
        new EmptySpace().addTo(fields);
        errDescription = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        txtDescription.setTextChangeListener((newText, changedText) -> validate());
        new Label("Type:").addTo(fields);
        List<Question.Type> sortedTypes = new ArrayList<>(List.of(Question.Type.values()));
        sortedTypes.sort(Comparator.comparing(Enum::name));
        cboType = new ComboBox<>(sortedTypes).addTo(fields);
        if (question != null) {
            cboType.setSelectedItem(question.getType());
        }
        new EmptySpace().addTo(fields);
        new EmptySpace().addTo(fields);
        new Label("Required:").addTo(fields);
        chkRequired = new CheckBox().addTo(fields);
        if (question != null) {
            chkRequired.setChecked(question.getRequired());
        }
        new EmptySpace().addTo(fields);
        new EmptySpace().addTo(fields);
        new Label("Options List:").addTo(fields);
        Panel optionListPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        cboOptionList = new ComboBox<OptionList>().addTo(optionListPanel);
        if (question != null) {
            for(OptionList optionList : OptionList.getAll()){
                if (optionList.getOwner() == null || optionList.getOwner().equals(getLoggedUser())){

                    cboOptionList.addItem(optionList);
                }
            }
            if(question.getOptionList()!= null)
                cboOptionList.setSelectedItem(OptionList.getByKey(question.getOptionListId()));
        }else {
            for(OptionList optionList : OptionList.getAll()){
                if (optionList.getOwner() == null || optionList.getOwner().equals(getLoggedUser())){

                    cboOptionList.addItem(optionList);
                }
            }
            cboOptionList.setSelectedItem(null);
        }

        btnAdd = new Button("Add", this::add).addTo(optionListPanel);
        optionListPanel.addTo(fields);
        new EmptySpace().addTo(fields);
        errOptionList = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        new EmptySpace().addTo(fields);
        Panel fiels = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        root.addComponent(new EmptySpace(), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        if(question != null) {
            Button btnDeleteQuestion = new Button("Delete", this::deleteQuestion).addTo(buttons);
        }
        btnCreate = new Button("Create", this::create);
        if(question != null) {
            Button btnUpdate = new Button("Update",this::updateQuestion).addTo(buttons);
        }else{
            btnCreate.addTo(buttons);
        }
        new Button("Cancel", this::close).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        validate();
    }

    private void updateQuestion() {
        question.setTitle(txtTitle.getText());
        question.setDescription(txtDescription.getText());
        question.setType(cboType.getSelectedItem());
        question.setRequired(chkRequired.isChecked());
        if(controller.update(question, cboOptionList.getSelectedItem())){
            reloadAfterDelete();
        }
    }

    private void deleteQuestion() {
        if (askConfirmation("You are about to delete this question. Please confirm.","Delete question")){
            question.delete();
            reloadAfterDelete();
        }
    }

    private void validate(){
        var errors = controller.validate(
                txtTitle.getText(),
                txtDescription.getText(),
                cboOptionList.getSelectedItem(),
                cboType.getSelectedItem()
        );
        errTitle.setText(errors.getFirstErrorMessage(Question.Fields.Title));
        errDescription.setText(errors.getFirstErrorMessage(Question.Fields.Description));
        Question.Type selectedType = cboType.getSelectedItem();
        String optionListError = "";
        if(selectedType != null && selectedType.requiresOptionList()){
            cboOptionList.setEnabled(true);
        }else {
            cboOptionList.setEnabled(false);
            if (cboOptionList.getSelectedItem() != null){
                cboOptionList.setSelectedItem(null);
            }
        }
        if (selectedType != null && selectedType.requiresOptionList() && cboOptionList.getSelectedItem() == null) {
            optionListError = "required for this type";
        }
        if (!optionListError.isEmpty()) {
            errOptionList.setText(optionListError);
        } else {
            errOptionList.setText("");
        }
        txtTitle.setTextChangeListener((newText, changedText) -> validate());
        txtDescription.setTextChangeListener((newText, changedText) -> validate());
        cboType.addListener((comboBox, previousItem, newItem) -> validate());
        cboOptionList.addListener((comboBox, previousItem, newItem) -> validate());
        errOptionList.setText(optionListError);

    }

    private void create() {
        if(controller.create(
                form.getId(),
                form.getNextIdx(),
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getSelectedItem(),
                chkRequired.isChecked(),
                cboOptionList.getSelectedItem()
        )){

            reloadAfterDelete();
        }
    }

    private void add() {
        AddEditOptionListController controller = new AddEditOptionListController(manageOptionListsView);
        Controller.navigateTo(controller);
        System.out.println("test");
        cboOptionList.clearItems();
        for(OptionList optionList : OptionList.getAll()){
            if (optionList.getOwner() == null || optionList.getOwner().equals(getLoggedUser())){

                cboOptionList.addItem(optionList);
            }
        }
        cboOptionList.setSelectedItem(controller.getOptionList());
    }

    private void reloadAfterDelete(){
        formView.close();
        close();
        controller.navigateTo(new ViewFormController(form));
    }
}
