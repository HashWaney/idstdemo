package com.hash.greendao.model;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by HashWaney on 2019/8/23.
 */

public class NoteTypeConvert implements PropertyConverter<NoteType, String> {
    @Override
    public NoteType convertToEntityProperty(String databaseValue) {
        return NoteType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(NoteType entityProperty) {
        return entityProperty.name();
    }
}
