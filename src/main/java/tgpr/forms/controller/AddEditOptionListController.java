package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditOptionListView;

import tgpr.forms.view.ManageOptionListsView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

import java.util.Comparator;
import java.util.List;

import static org.mariadb.jdbc.pool.Pools.close;
import static tgpr.forms.model.Security.getLoggedUser;


public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private AddEditOptionListView view;

    public AddEditOptionListController(ManageOptionListsView view) {
        this.view = new AddEditOptionListView(this, view);
    }

    public AddEditOptionListController(OptionList optionList, ManageOptionListsView view) {
        this.view = new AddEditOptionListView(this,optionList,view);
    }

    public AddEditOptionListView getView() {
        return view;
    }



    public void delete(OptionList optionList,AddEditOptionListView addEditOptionListView){
        boolean confirmed = askConfirmation("Are you sure you want to delete this option list?", "Delete");
        if (confirmed) {
            optionList.delete();
            addEditOptionListView.close();
            navigateTo(new ManageOptionListsController());
        }
    }

    public void duplicate(OptionList optionList,AddEditOptionListView addEditOptionListView, ManageOptionListsView manageOptionListsView){
        optionList.duplicate(getLoggedUser());
        addEditOptionListView.close();
        manageOptionListsView.close();
        navigateTo(new ManageOptionListsController());
    }

    public void save(OptionList optionList,List<OptionValue> listOfOptionValues,String newName, boolean checked){
        optionList.deleteAllValues();
        optionList.addValues(listOfOptionValues);
        optionList.setName(newName);
        check(optionList,checked);
        optionList.save();
    }

    public void create(OptionList optionList, List<OptionValue> listOfOptionValues,String newName, boolean checked){
        optionList.setName(newName);
        check(optionList,checked);
        optionList.save();
        optionList.addValues(listOfOptionValues);
    }


    public void orderAlphabetically(List<OptionValue> listOfOptionValues){
        listOfOptionValues.sort(Comparator.comparing(option -> option.getLabel().toLowerCase()));
    }

    public void check(OptionList optionList, boolean checked){
        if (checked) {
            optionList.setOwnerId(null);
        } else {
            optionList.setOwnerId(getLoggedUser().getId());
        }
    }


    private boolean optionListWithThisNameAlreadyExistsForUser(OptionList optionList, String name, boolean editOptionListMode, boolean newOptionListMode){
        List<OptionList> listForUser = optionList.getForUser(getLoggedUser());
        for (OptionList l : listForUser) {
            if (l.getName().equals(name)) {
                if (editOptionListMode && optionList.getId() != l.getId()) {
                    return true;
                }
                else if (newOptionListMode) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean optionValueWithThisLabelAlreadyExistsInThisOptionList(List<OptionValue> listOfOptionValues, List<OptionValue> listOfAddedOptionValues, String label){
        for (OptionValue o : listOfOptionValues) {
            if (o.getLabel().equals(label)) {
                return true;
            }
        }
        for (OptionValue o : listOfAddedOptionValues) {
            if (o.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }


    public ErrorList validate(OptionList optionList, List<OptionValue> listOfOptionValues, List<OptionValue> listOfAddedOptionValues, String newOptionListName, String newValueLabel,boolean atLeastOneValue, boolean editOptionListMode, boolean newOptionListMode) {
        var errors = new ErrorList();

        // Vérifier que le nom de l'option list est donné
        if (newOptionListName.length() == 0){
            errors.add("name required", OptionList.Fields.Name);
        }

        // Vérifier que le nom de l'option list contient au moins 3 caractères
        if (newOptionListName.length() < 3){
            errors.add("minimum 3 char", OptionList.Fields.Name);
        }

        // Vérifier que le couple (owner, name) est unique
        if (optionListWithThisNameAlreadyExistsForUser(optionList, newOptionListName, editOptionListMode, newOptionListMode)){
            errors.add("an option list with this name already exists for user", OptionList.Fields.Name);
        }

        // Vérifier qu'au moins une valeur existe dans l'option list (dans le tableau)
        if (!atLeastOneValue){
            errors.add("at least one value required", OptionList.Fields.Values);
        }

        // Vérifier que le label de la valeur ajoutée contient au moins 3 caractères
        if (newValueLabel.length() > 0 && newValueLabel.length() < 3){
            errors.add("minimum 3 char", OptionValue.Fields.Label);
        }

        // Vérifier que le couple (option_list, label) est unique
        if (optionValueWithThisLabelAlreadyExistsInThisOptionList(listOfOptionValues, listOfAddedOptionValues, newValueLabel)){
            errors.add("an option value with this label already exists for this option list", OptionValue.Fields.Label);
        }

        return errors;
    }


}


