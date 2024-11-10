package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.view.ViewInstancesView;
import tgpr.framework.Controller;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewInstancesController extends Controller<ViewInstancesView> {
    private final ViewInstancesView view;
    private final Form form;
    private Instance selectedInstance;

    @Override
    public ViewInstancesView getView() { return view; }

    public ViewInstancesController(Form form) {
        this.form = form;
        view = new ViewInstancesView(this, form);
    }

    public List<Instance> getSubmittedInstances() {
        // récupère toutes les instances completed du formulaire, les trie dans l'ordre inverse de leur date de soumission
        return form.getInstances().stream().filter(Instance::isCompleted).sorted(Comparator.comparing(Instance::getCompleted).reversed()).collect(Collectors.toList());
    }

    public void viewEditInstance(Instance instance){
        navigateTo(new ViewEditInstanceController(form, instance));
    }

    public void deleteAll(){
        if (askConfirmation("Are you sure you want to delete all the submitted instances?\n" +
                        "Note: This will not delete instances that are currently being edited (not submitted).",
                "Delete All Instances")) {
            form.deleteAllSubmittedInstances();
        }
    }

    public void deleteInstance(Instance instance){
        if (askConfirmation("Are you sure you want to delete this instance?", "Delete Instance")) {
            instance.delete();
        }
    }

    public void setSelectedInstance(Instance instance){
        selectedInstance = instance;
    }

    public void deleteSelectedInstance() {
        if (selectedInstance != null) {
            deleteInstance(selectedInstance);
            view.refresh();
        }
    }

    public void closeView() {
        view.close();
        navigateTo(new AnalyzeController(form));
    }

}
