package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;
import tgpr.forms.view.AddEditOptionListView;

import tgpr.framework.Controller;


public class AddEditOptionListController extends Controller<AddEditOptionListView> {
    private AddEditOptionListView view;
    private OptionList optionList;
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
    public void add(){}

    public void reorder(){}
    public void delete(){}
    public void save(){}
    public void close(){}



    public AddEditOptionListView getView() {
        //return new AddEditOptionListView(this);
        return view;
    }

}


