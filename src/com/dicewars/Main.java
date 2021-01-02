package com.dicewars;

import com.dicewars.Controllers.WindowController;
import com.dicewars.Models.WindowModel;
import com.dicewars.Views.WindowView;

public class Main {

    static public void main(String[] args) {
        WindowModel windowModel = new WindowModel();
        WindowView windowView = new WindowView(windowModel);
        new WindowController(windowModel, windowView);
    }
}
