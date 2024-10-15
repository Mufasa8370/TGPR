package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.ObjectTable;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.forms.view.AnalyzeView;
import tgpr.framework.Controller;
import tgpr.forms.model.Instance;
import java.util.List;

public class AnalyzeController extends Controller<AnalyzeView> {
    private final AnalyzeView view;
    private final Form form;

    @Override
    public AnalyzeView getView() { return view; }

    public AnalyzeController(Form form) {
        this.form = form;
        view = new AnalyzeView(this, form);
    }

    public long getNbSubmittedInstances() {
        return form.getInstances().stream().filter(Instance::isCompleted).count();
    }

    public void questionsPanel(ObjectTable<Object> statisticsTable) {
        List<Question> questions = form.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            statisticsTable.getTableModel().addRow(String.valueOf(question.getIdx()), question.getTitle());
        }
    }

}
