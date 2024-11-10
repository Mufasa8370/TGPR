package tgpr.forms.view;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.QuestionController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static tgpr.framework.Controller.askConfirmation;
import static tgpr.framework.Controller.navigateTo;

public class QuestionView extends DialogWindow {

    private final QuestionController controller;
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

    public QuestionView(QuestionController controller, Question question) {
        super(question == null ? "Add a question" : "Edit a question");

        this.controller = controller;
        this.question = question;

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
            cboOptionList.setSelectedItem(question.getOptionList());  // Pré-remplir le champ titre
        }
        btnAdd = new Button("Add", this::add).addTo(optionListPanel);
        optionListPanel.addTo(fields);
        new EmptySpace().addTo(fields);
        errOptionList = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        new EmptySpace().addTo(fields);
        errOptionList = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
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
            btnCreate.setEnabled(false);
            btnCreate.addTo(buttons);
        }
        new Button("Cancel", this::close).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
    }

    private void updateQuestion() {
        controller.save(
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getSelectedItem(),
                chkRequired.isChecked(),
                cboOptionList.getSelectedItem()
        );
    }

//    private Question updateQuestion() {
//        var controller = new QuestionController(question);
//        navigateTo(controller);
//        return controller.getQuestion();
//        //refresh();
//    }

    private void deleteQuestion() {
        if (askConfirmation("You are about to delete this question. Please confirm.","Delete question")){
            question.delete();
            close();
        }
    }

    private void validate(){
        var errors = controller.validate(
                txtTitle.getText(),
                txtDescription.getText()
        );
        errTitle.setText(errors.getFirstErrorMessage(Question.Fields.Title));
        errDescription.setText(errors.getFirstErrorMessage(Question.Fields.Description));
        Question.Type selectedType = cboType.getSelectedItem();
        String optionListError = "";
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
        boolean isFormValid = errors.isEmpty() && optionListError.isEmpty();
        btnCreate.setEnabled(isFormValid);
    }

    private void create() {
        controller.create(
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getSelectedItem(),
                chkRequired.isChecked(),
                cboOptionList.getSelectedItem()
        );
    }

    private void add() {
        Controller.showMessage("use case en cours de dev", "message", "ok");
    }
}
