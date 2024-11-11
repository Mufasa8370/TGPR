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

    public static String cutText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public long getNbSubmittedInstances() {
        return form.getInstances().stream().filter(Instance::isCompleted).count();
    }

    public void questionsPanel(ObjectTable<Question> questionsTable) {
        questionsTable.add(form.getQuestions());
    }

    public void answersPanel(ObjectTable<Stat> answersTable, Question question) {
        answersTable.clear();

        List<ValueStat> listAnswersValueStat = question.getStats(); // value stat est une classe du modèle qui contient des données sur les réponses à une question
        DecimalFormat df = new DecimalFormat("0.0%");
        long nbAnswers = getNbSubmittedInstances();

        for (ValueStat valueStat : listAnswersValueStat) {
            Stat stat = new Stat(); // j'ai créé un nouvel objet Stat dans le modèle pour stocker les données sur les réponses à une question
            stat.setValue(valueStat.getValue());
            stat.setCount(valueStat.getCount());
            double ratio = (double) valueStat.getCount() / nbAnswers;
            stat.setRatio(df.format(ratio));
            answersTable.add(stat); // j'ai ajouté l'objet Stat à la table des réponses
        }
    }

    public void viewInstances() {
        // (pour le système de refresh : étape 1) on passe l'objet courant this qui représente le contrôleur AnalyzeController à la vue ViewInstancesView
        navigateTo(new ViewInstancesController(form, this));
    }

    public void refreshView() {
        view.refresh();  // (pour le système de refresh : étape 2) on rafraîchit la vue AnalyzeView
    }

    public void closeView() {
        view.close();
        navigateTo(new ViewFormController(form)); // ici je n'utilise pas le meme système de refresh car je close la vue View Form quand analyze est appelé depuis View Form
        // je peux donc simplement naviguer vers un nouveau contrôleur ViewFormController. je peux me permettre de faire cela car la fenetre analyse est plus grande que la fenetre view form
    }

}
