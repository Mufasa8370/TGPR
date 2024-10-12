package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.LoginController;
import tgpr.forms.controller.ViewFormsController;

import com.googlecode.lanterna.gui2.*;
import tgpr.framework.Configuration;
import tgpr.forms.controller.LoginController;

import java.util.List;

public class ViewFormsView extends BasicWindow{
    private final ViewFormsController controller;
    public ViewFormsView(ViewFormsController controller) {
        this.controller = controller;

        setTitle("Login");
        setHints(List.of(Window.Hint.CENTERED));

        Panel root = new Panel();
        setComponent(root);

        Panel panel = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1).setVerticalSpacing(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        panel.addComponent(new Label("Mail:"));

        new EmptySpace().addTo(root);

    }
}
