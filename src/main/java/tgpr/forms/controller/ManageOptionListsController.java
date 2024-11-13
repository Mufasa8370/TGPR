package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.view.ManageOptionListsView;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.framework.Controller;

import java.util.List;


public class ManageOptionListsController extends Controller<ManageOptionListsView>{
    private final ManageOptionListsView view;
    //private List<OptionList> listOfOptionLists;


    public ManageOptionListsController() {

        this.view = new ManageOptionListsView(this);
    }

    @Override
    public ManageOptionListsView getView() {
        return view;
    }

    public void newList(){
        navigateTo(new AddEditOptionListController());
    }
    public void close(){
        navigateTo(new ViewFormsController());
    }

    public void editList(OptionList optionList){
        navigateTo(new AddEditOptionListController(optionList,view));
    }

    public int getNumberOfCopies(OptionList optionList){
        int nb = 0;
        List<OptionList> listOfOptionLists = OptionList.getAll();
        for (OptionList o : listOfOptionLists) {
            if (o.equals(optionList)) {
                ++nb;
            }
        }
        return nb;
    }


}


