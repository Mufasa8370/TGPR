package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.ObjectTable;
import tgpr.forms.model.*;
import tgpr.forms.view.AnalyzeView;
import tgpr.framework.Controller;
import java.text.DecimalFormat;
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

    public void questionsPanel(ObjectTable<Question> questionsTable) {
        questionsTable.add(form.getQuestions());
    }

    public void answersPanel(ObjectTable<Stat> answersTable, Question question) {
        answersTable.clear();

        List<ValueStat> listAnswersValueStat = question.getStats();
        DecimalFormat df = new DecimalFormat("0.0%");
        long nbAnswers = getNbSubmittedInstances();

        for (ValueStat valueStat : listAnswersValueStat) {
            Stat stat = new Stat();
            stat.setValue(valueStat.getValue());
            stat.setCount(valueStat.getCount());
            double ratio = (double) valueStat.getCount() / nbAnswers;
            stat.setRatio(df.format(ratio));
            answersTable.add(stat);
        }
    }

    public void viewInstances() {
        navigateTo(new ViewInstancesController(form));
    }

}
