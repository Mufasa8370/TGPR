package tgpr.forms.controller;

import tgpr.forms.model.*;
import tgpr.forms.view.AddEditOptionListView;

import tgpr.framework.Controller;

import java.util.List;


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


    /*
            public Member save() {
                var controller = new EditMemberController(member);
                navigateTo(controller);
                return controller.getMember();
            }

             */
    /*
    public void add(OptionValue value) {
        optionList.addValue(value);
    }

     */
    public void add(OptionList optionList, List<OptionValue> listOfAddedOptionValues) {
        optionList.addValues(listOfAddedOptionValues);
    }

    public void reorder(OptionList optionList){}
    public void delete(OptionList optionList){
        boolean confirmed = askConfirmation("Are you sure you want to delete this option list?", "Delete");
        if (confirmed) {
            optionList.delete();
        }
    }

    public void duplicate(OptionList optionList,User forUser){
        optionList.duplicate(forUser);
    }
    public void save(OptionList optionList){
        optionList.save();
    }

    public boolean close(List<OptionValue> listOfAddedOptionValues){
        if (!listOfAddedOptionValues.isEmpty()) {
            boolean confirmed = askConfirmation("Are you sure you want to cancel?", "Cancel");
            if (confirmed) {
                navigateTo(new ManageOptionListsController());
                return true;
            }
        }
        else {
            navigateTo(new ManageOptionListsController());
            return true;
        }
        return false;
    }




}


