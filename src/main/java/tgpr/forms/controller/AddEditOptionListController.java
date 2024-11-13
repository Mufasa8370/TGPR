package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditOptionListView;

import tgpr.forms.view.ManageOptionListsView;
import tgpr.framework.Controller;

import java.util.Comparator;
import java.util.List;

import static org.mariadb.jdbc.pool.Pools.close;
import static tgpr.forms.model.Security.getLoggedUser;


public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private AddEditOptionListView view;

    public AddEditOptionListController() {
        this.view = new AddEditOptionListView(this);
    }

    public AddEditOptionListController(OptionList optionList, ManageOptionListsView view) {
        this.view = new AddEditOptionListView(this,optionList,view);
    }

    public AddEditOptionListView getView() {
        return view;
    }


    public void addForSave(OptionList optionList, List<OptionValue> listOfAddedOptionValues) {
        optionList.addValues(listOfAddedOptionValues);
    }

    public void reorder(OptionList optionList){}

    public void delete(OptionList optionList,AddEditOptionListView addEditOptionListView){
        boolean confirmed = askConfirmation("Are you sure you want to delete this option list?", "Delete");
        if (confirmed) {
            optionList.delete();
            addEditOptionListView.close();
            navigateTo(new ManageOptionListsController());
        }
    }

    public void duplicate(OptionList optionList,AddEditOptionListView addEditOptionListView){
        optionList.duplicate(getLoggedUser());
        addEditOptionListView.close();
        navigateTo(new ManageOptionListsController());
    }

    public void save(OptionList optionList, List<OptionValue> listOfAddedOptionValues,String newName, boolean checked){
        optionList.addValues(listOfAddedOptionValues);
        optionList.setName(newName);
        check(optionList,checked);
        optionList.save();
    }

    public void create(OptionList optionList, List<OptionValue> listOfAddedOptionValues,String newName){
        optionList.setName(newName);
        optionList.setOwnerId(getLoggedUser().getId());
        optionList.save();
        optionList.addValues(listOfAddedOptionValues);
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




}


