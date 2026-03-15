package com.volter.backend.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificationChange {
    private Object oldValue;
    private Object newValue;
}
