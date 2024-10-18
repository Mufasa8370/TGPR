package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.ObjectTable;
import tgpr.forms.model.*;
import tgpr.forms.view.AnalyzeView;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.HashSet;
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

    public void answersPanel(ObjectTable<Answer> answersTable, Question question) {
//        List<ValueStat> listAnswersValueStat = question.getStats();
//        System.out.println(listAnswersValueStat);

        answersTable.clear();
        List<Answer> listAnswersWithoutDoublons = getAnswersWithoutDoublons(question);
        answersTable.add(listAnswersWithoutDoublons);
    }

    public List<Answer> getAnswersWithoutDoublons(Question question) {
        List<Answer> listAnswersWithDoublons = question.getAnswers();
        List<Answer> listAnswersWithoutDoublons = new ArrayList<>();
        HashSet<String> seenValues = new HashSet<>();

        for (Answer answer : listAnswersWithDoublons) {
            if (seenValues.add(answer.getValue())) {
                listAnswersWithoutDoublons.add(answer);
            }
        }

        return listAnswersWithoutDoublons;
    }

}
