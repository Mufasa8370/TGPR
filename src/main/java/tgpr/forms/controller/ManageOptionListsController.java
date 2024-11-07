package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.view.ManageOptionListsView;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.framework.Controller;


public class ManageOptionListsController extends Controller<ManageOptionListsView>{
    private final ManageOptionListsView view;

    public ManageOptionListsController() {
        this.view = new ManageOptionListsView(this);
    }

    @Override
    public ManageOptionListsView getView() {
        return view;
    }

    public void newList(){
        //navigateTo(new AddEditOptionList());
    }
    public void close(){
        navigateTo(new ViewFormsController());
    }
    public void editList(OptionList optionList){
        //navigateTo(new AddEditOptionList(optionList));
    }


}


