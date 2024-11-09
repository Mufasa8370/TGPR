package tgpr.forms;

import tgpr.forms.controller.LoginController;
import tgpr.forms.controller.ManageDistListsController;
import tgpr.forms.controller.TestController;
import tgpr.forms.controller.ViewInstancesController;
import tgpr.forms.model.Form;
import tgpr.framework.Controller;
import tgpr.framework.Model;

import javax.swing.text.View;

public class FormsApp {
    public final static String DATABASE_SCRIPT_FILE = "/database/tgpr-2425-f03.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else {
            Controller.navigateTo(new ManageDistListsController());
        }
    }
}
