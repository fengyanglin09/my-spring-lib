package io.github.fengyanglin09.spa.alert.autoconfigure.service;

import io.github.fengyanglin09.spa.alert.autoconfigure.model.ExceptionAlertContext;

/**
 * this is what other app will provide or implement
 * */
public interface ExceptionAlertService {
    void sendAlert(ExceptionAlertContext context);
}
