package tgpr.forms;

import tgpr.forms.controller.*;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;
import tgpr.framework.Model;

import javax.swing.text.View;

public class FormsApp {
    public final static String DATABASE_SCRIPT_FILE = "/database/tgpr-2425-f03.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else {
            Form form = Form.getByKey(2);
            Controller.navigateTo(new ViewFormController(form));
            //Controller.navigateTo(new LoginController());
        }
    }
}
