package org.kraskovsky.polls.service;

import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.User;

import java.util.List;

public interface FieldService {

    void addField(User user, Field field);

    List<Field> getFields(User user);

    void removeField(User user, String name);

    void updateField(User user, Field field);
}
