package com.ncr.interns.codecatchers.incredicabs.NCABUtils;

/**
 * Created by gs250365 on 3/13/2018.
 */



import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created by sj250128 on 3/29/2017.
 * This file is under JIRA GES-21860 India UAT - Android OS 7.1 is having a different clock for the Sch. Start/End & Travel/Labor :: Summved
 * Workaround for this bug: https://code.google.com/p/android/issues/detail?id=222208
 * In Android 7.0 Nougat, spinner mode for the DatePicket in DatePickerDialog is
 * incorrectly displayed as clock, even when the theme specifies otherwise, such as:
 *
 */

public class DatePickerUtilClass extends DatePickerDialog {

    public DatePickerUtilClass(Context context, int theme, OnDateSetListener callBack,
                               int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);

        // Force spinners on Android 7.0 only (SDK 24).
        // Note: I'm using a naked SDK value of 24 here, because I'm
        // targeting SDK 23, and Build.VERSION_CODES.N is not available yet.
        // But if you target SDK >= 24, you should have it.
        if (Build.VERSION.SDK_INT == 24) {
            try {
                final Field field = this.findField(
                        DatePickerDialog.class,
                        DatePicker.class,
                        "mDatePicker"
                );

                final DatePicker datePicker = (DatePicker) field.get(this);
                final Class<?> delegateClass = Class.forName(
                        "android.widget.DatePicker$DatePickerDelegate"
                );
                final Field delegateField = this.findField(
                        DatePicker.class,
                        delegateClass,
                        "mDelegate"
                );

                final Object delegate = delegateField.get(datePicker);
                final Class<?> spinnerDelegateClass = Class.forName(
                        "android.widget.DatePickerSpinnerDelegate"
                );

                if (delegate.getClass() != spinnerDelegateClass) {
                    delegateField.set(datePicker, null);
                    datePicker.removeAllViews();

                    final Constructor spinnerDelegateConstructor =
                            spinnerDelegateClass.getDeclaredConstructor(
                                    DatePicker.class,
                                    Context.class,
                                    AttributeSet.class,
                                    int.class,
                                    int.class
                            );
                    spinnerDelegateConstructor.setAccessible(true);

                    final Object spinnerDelegate = spinnerDelegateConstructor.newInstance(
                            datePicker,
                            context,
                            null,
                            android.R.attr.datePickerStyle,
                            0
                    );
                    delegateField.set(datePicker, spinnerDelegate);

                    datePicker.init(year, monthOfYear, dayOfMonth, this);
                    datePicker.setCalendarViewShown(false);
                    datePicker.setSpinnersShown(true);
                }
            } catch (Exception e) { /* Do nothing */ }
        }
    }

    /**
     * Find Field with expectedName in objectClass. If not found, find first occurrence of
     * target fieldClass in objectClass.
     */
    private Field findField(Class objectClass, Class fieldClass, String expectedName) {
        try {
            final Field field = objectClass.getDeclaredField(expectedName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) { /* Ignore */ }

        // Search for it if it wasn't found under the expectedName.
        for (final Field field : objectClass.getDeclaredFields()) {
            if (field.getType() == fieldClass) {
                field.setAccessible(true);
                return field;
            }
        }

        return null;
    }
}

