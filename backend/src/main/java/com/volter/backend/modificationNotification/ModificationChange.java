package com.volter.backend.modificationNotification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificationChange {
    private Object oldValue;
    private Object newValue;
}
