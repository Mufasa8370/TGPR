package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.*;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;
import java.util.List;

import static tgpr.framework.Controller.askConfirmation;

public class ViewFormView extends DialogWindow {

    private final ViewFormController controller;
    private final Form form;
    private final Label lblTitle;
    private final Label lblDescription;
    private final Label lblCreatedBy;
    private final Label lblIsPublic;
    public final Panel pnlQuestions;
    private final ObjectTable<Question> questionTable;
    private boolean existInstance;
    private Question question;
    private Label lblNoQuestions;
    private boolean auto = false;

    List<Question> questions;

    Question current;
    private Button saveOrder,cancel,btnNewQuestion, btnEditForm, btnDeleteForm, btnMakePrivate, btnMakePublic,btnShare, btnClearInstances, btnReorder, btnAnalyse, close;


    private boolean reorderMode = false,reoderSelect = false;

    public ViewFormView(ViewFormController controller, Form form) {
        super("View Form Details");

        this.controller = controller;
        this.form = form;

        if(form.getInstances().isEmpty()){
            existInstance = false;
        }else{
            existInstance = true;
        }

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(80, 18));

        Panel root = new Panel();
        setComponent(root);

        Panel fields = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        fields.addComponent(new Label("Title:"));
        lblTitle = new Label(form.getTitle()).addTo(fields);

        fields.addComponent(new Label("Description:"));
        lblDescription = new Label(form.getDescription()).addTo(fields);

        fields.addComponent(new Label("Created By:"));
        lblCreatedBy = new Label(String.valueOf(form.getOwner())).addTo(fields);

        fields.addComponent(new Label("Is Public:"));
        lblIsPublic = new Label(String.valueOf(form.getIsPublic())).addTo(fields);

        if(existInstance){
            root.addComponent(new Label("This form is read only because it has already been answered("+form.getInstances().size()+" instance(s)).").setForegroundColor(TextColor.ANSI.BLUE));
        }

        pnlQuestions = new Panel();
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx ).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Title", Question::getTitle).setWidth(30).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Type", Question::getType).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Required", Question::getRequired).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Option List", Question::getOptionList).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap)
        ).addTo(pnlQuestions);

        questions = form.getQuestions();

        if(!existInstance){
            questionTable.setSelectAction(() -> {
                if (!reorderMode){
                    var selectedQuestion = questionTable.getSelected();
                    if (selectedQuestion != null) {
                        Controller.navigateTo(new AddEditQuestionController(selectedQuestion, form, this)); // Assurez-vous d'ajouter cette méthode dans le contrôleur.
                        refresh();
                    }

                }else {
                    reorderSelect();
                }

            });
        }

        lblNoQuestions = new Label("No question yet").setForegroundColor(TextColor.ANSI.RED);
        lblNoQuestions.setVisible(false);
        lblNoQuestions.addTo(pnlQuestions);

        if (questions == null || questions.isEmpty()) {
            lblNoQuestions.setVisible(true);
        } else {
            lblNoQuestions.setVisible(false);
            questionTable.add(questions);
        }

        pnlQuestions.setPreferredSize(new TerminalSize(500,500));
        root.addComponent(pnlQuestions);

        new EmptySpace().addTo(fields);

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        if(!existInstance){
            btnNewQuestion = new Button("New Question", this::newQuestion).addTo(buttons);
            btnEditForm = new Button("Edit Form", this::editForm).addTo(buttons);
        }
        btnDeleteForm = new Button("Delete Form", this::deleteForm).addTo(buttons);
        if(existInstance){
            if(form.getIsPublic()){
                btnMakePrivate = new Button("Make Private", this::makePublic).addTo(buttons);
            }else{
                btnMakePublic = new Button("Make Public", this::makePublic).addTo(buttons);
            }
        }
        btnShare = new Button("Share", this::share).addTo(buttons);
        if(existInstance){
            btnClearInstances = new Button("Clear Instances",this::clearInstances).addTo(buttons);
        }
        if(!existInstance){
            btnReorder = new Button("Reorder", this::reorder).addTo(buttons);
        }

        if (existInstance) {
            btnAnalyse = new Button("Analyse", this::analyse).addTo(buttons);
        }
        close = new Button("Close", this::close).addTo(buttons);
        saveOrder = new Button("Save Order", this::saveOrder).addTo(buttons).setVisible(false);
        cancel = new Button("Cancel", this::cancel).addTo(buttons).setVisible(false);

        root.addComponent(buttons);
    }

    private void cancel() {
        close();
        controller.navigateTo(new ViewFormController(form));
    }

    private void clearInstances() {
        boolean confirmed = askConfirmation("Are you sure you want to delete all instances? " +
                "Note: This will also delete instances currently being edited (not submitted).", "Delete All Instances");
        if (confirmed) {
            form.deleteAllInstances();
            close();
            controller.navigateTo(new ViewFormController(form));
        }
    }

    private void makePublic() {
        form.setIsPublic(!form.getIsPublic());
        form.save();
        //temp
        close();
        Controller.navigateTo(new ViewFormController(form));
    }

    private void analyse() {
        Controller.navigateTo(new AnalyzeController(form));
    }

    private void reorder() {
        reorderMode = true;

        if (btnNewQuestion != null) {
            btnNewQuestion.setVisible(false);
        }
        if (btnEditForm != null) {
            btnEditForm.setVisible(false);
        }
        if (btnDeleteForm != null) {
            btnDeleteForm.setVisible(false);
        }
        if (btnMakePrivate != null) {
            btnMakePrivate.setVisible(false);
        }
        if (btnMakePublic != null) {
            btnMakePublic.setVisible(false);
        }
        if (btnShare != null) {
            btnShare.setVisible(false);
        }
        if (btnClearInstances != null) {
            btnClearInstances.setVisible(false);
        }
        if (btnReorder != null) {
            btnReorder.setVisible(false);
        }
        if (btnAnalyse != null) {
            btnAnalyse.setVisible(false);
        }
        if (close != null) {
            close.setVisible(false);
        }
        saveOrder.setVisible(true);
        cancel.setVisible(true);
    }
    public void changeOrder(List<Question> questions, int oldInd, int newInd){
        Question old = questions.get(oldInd);
        Question newQustion = questions.get(newInd);
        questions.set(oldInd,newQustion);
        questions.set(newInd,old);

    }
    private void saveOrder() {
        form.reorderQuestions(questions);
        close();
        controller.navigateTo(new ViewFormController(form));
    }
    private void reorderSelect() {
        if(!reoderSelect){
            current = questionTable.getSelected();
            questionTable.addSelectionChangeListener(new ObjectTable.SelectionChangeListener() {
                @Override
                public void onSelectionChanged(int oldRow, int newRow, boolean byUser) {
                    if(!auto){
                        changeOrder(questions,oldRow,newRow);
                        questionTable.clear();
                        questionTable.add(questions);
                        auto = true;
                        questionTable.setSelected(questions.get(newRow));
                    }else {
                        auto = false;
                    }
                }
            });
            reoderSelect = true;
        }else {

            reoderSelect = false;
            questionTable.addSelectionChangeListener(new ObjectTable.SelectionChangeListener() {
                @Override
                public void onSelectionChanged(int oldRow, int newRow, boolean byUser) {
                    System.out.println("okok");
                }
            });
        }

    }

    private void share() {
        Controller.navigateTo(new ManageSharesController());
    }

    private void deleteForm() {
        controller.delete();
    }

    private void editForm() {
        Controller.navigateTo(new EditFormController());
    }

    private void newQuestion() {
        controller.newQuestion();
    }

    private void refresh() {
        if(question != null){

            var questions = Question.getAll();
            pnlQuestions.removeAllComponents();
            if (questions.isEmpty()) {
                pnlQuestions.addComponent(lblNoQuestions);
            }else{
                pnlQuestions.addComponent(questionTable);
                questionTable.clear();
                questionTable.add(questions);
            }
        }
    }
}
