package tgpr.forms;

import tgpr.forms.controller.TestController;
import tgpr.framework.Controller;
import tgpr.framework.Model;

public class FormsApp {
    public final static String DATABASE_SCRIPT_FILE = "/database/tgpr-2425-f03.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else {
            Controller.navigateTo(new TestController());
        }
    }
}
