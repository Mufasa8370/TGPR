package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditOptionListView;

import tgpr.framework.Controller;

import java.util.List;

import static org.mariadb.jdbc.pool.Pools.close;
import static tgpr.forms.model.Security.getLoggedUser;


public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private AddEditOptionListView view;
    private OptionList optionList;

    public AddEditOptionListController() {
        this.view = new AddEditOptionListView(this);
    }

    public AddEditOptionListController(OptionList optionList) {
        this.view = new AddEditOptionListView(this,optionList);
    }

    public AddEditOptionListView getView() {
        return view;
    }


    public void addForSave(OptionList optionList, List<OptionValue> listOfAddedOptionValues) {
        optionList.addValues(listOfAddedOptionValues);
    }

    public void reorder(OptionList optionList){}

    public void delete(OptionList optionList){
        boolean confirmed = askConfirmation("Are you sure you want to delete this option list?", "Delete");
        if (confirmed) {
            optionList.delete();
            close();
            navigateTo(new ManageOptionListsController());
        }
    }

    public void duplicate(OptionList optionList){
        optionList.duplicate(getLoggedUser());
        close();
        navigateTo(new ManageOptionListsController());
    }

    public void save(OptionList optionList, List<OptionValue> listOfAddedOptionValues){
        optionList.addValues(listOfAddedOptionValues);
        optionList.save();
    }

    /*
    public boolean closeForController(List<OptionValue> listOfAddedOptionValues){
        if (!listOfAddedOptionValues.isEmpty()) {
            boolean confirmed = askConfirmation("Are you sure you want to cancel?", "Cancel");
            if (confirmed) {
                close();
                navigateTo(new ManageOptionListsController());
                return true;
            }
        }
        else {
            close();
            navigateTo(new ManageOptionListsController());
            return true;
        }
        return false;
    }

     */




}


