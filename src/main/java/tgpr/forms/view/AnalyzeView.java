package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.AnalyzeController;
import tgpr.forms.model.Stat;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.ViewManager;
import java.util.List;

public class AnalyzeView extends DialogWindow {
    private final AnalyzeController controller;
    private Form form;
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblNbInstances = new Label("");
    private ObjectTable<Question> questionsTable;
    private ObjectTable<Stat> answersTable;
    private Panel pnlQuestions;
    private Panel pnlAnswers;

    public AnalyzeView(AnalyzeController controller, Form form){
        super("Statistical Analysis of Submitted Instances");
        this.controller = controller;
        this.form = form;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(90, 23));

        var root = Panel.verticalPanel();
        setComponent(root);

        createFields().addTo(root);
        createQuestionsAndAnswersPanel().sizeTo(ViewManager.getTerminalColumns(),17).addTo(root);
        createButtonsPanel().addTo(root);

        refresh();
    }

    private Panel createFields(){
        var panel = Panel.gridPanel(2, Margin.of(1,1,1,0));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);
        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);
        new Label("Number of Submitted Instances:").addTo(panel);
        lblNbInstances.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Panel createQuestionsAndAnswersPanel() {
        var panel = Panel.gridPanel(2, Margin.of(0));

        createQuestionsPanel().addTo(panel);
        createAnswersPanel().addTo(panel);

        return panel;
    }

    private Panel createQuestionsPanel() {
        var pnlQuestions = Panel.gridPanel(1, Margin.of(1));

        questionsTable = new ObjectTable<>(
                new ColumnSpec<Question>("Index", Question::getIdx)
                        .setMinWidth(5).alignRight(),
                new ColumnSpec<Question>("Title", Question::getTitleForAnalyze)
                        .setMinWidth(27).alignLeft()
        ).addTo(pnlQuestions);

        questionsTable.addSelectionChangeListener(this::onQuestionSelectionChanged);

        return pnlQuestions;
    }

    private void onQuestionSelectionChanged(int oldValue, int newValue, boolean byUser) {
        Question selectedQuestion = questionsTable.getSelected();
        if (selectedQuestion != null) {
            controller.answersPanel(answersTable, selectedQuestion); // va remplir la table de réponses
        }
    }

    private Panel createAnswersPanel() {
        var pnlAnswers = Panel.gridPanel(1, Margin.of(1));

        answersTable = new ObjectTable<>(
                new ColumnSpec<Stat>("Value", Stat::getValue )
                        .setMinWidth(24).alignLeft(),
                new ColumnSpec<Stat>("Nb Occ.", Stat::getCount)
                        .setMinWidth(3).alignRight(),
                new ColumnSpec<Stat>("Ratio", Stat::getRatio)
                        .setMinWidth(8).alignRight()
        ).addTo(pnlAnswers);

        return pnlAnswers;
    }

    private Panel createButtonsPanel() {
        var panel = Panel.horizontalPanel().center();

        Button btnClose = new Button("Close", this.controller::closeView).addTo(panel); // this fait référence à la vue AnalyzeView

        if (!form.getInstances().isEmpty()) {
            Button viewInstances = new Button("View Instances", this.controller::viewInstances).addTo(panel);
            addShortcut(viewInstances, KeyStroke.fromString("<A-v>"));
        }

        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));

        return panel;
    }

    public void refresh() {
        if (form != null) {
            // Met à jour Fields
            lblTitle.setText(form.getTitle());
            lblDescription.setText(form.getDescription());
            lblNbInstances.setText(String.valueOf(controller.getNbSubmittedInstances()));
            // Met à jour Questions
            questionsTable.clear();
            controller.questionsPanel(questionsTable); // va remplir la table de questions
            // Met à jour Answers
            onQuestionSelectionChanged(-1, questionsTable.getSelectedRow(), true);
                    // -1 signifie que la sélection précédente est inexistante
                    // questionsTable.getSelectedRow() pour obtenir la ligne sélectionnée
                    // true pour spécifier que la sélection est faite par l'utilisateur
        }
    }
}
