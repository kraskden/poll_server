package org.kraskovsky.polls.repository;

import org.kraskovsky.polls.model.Field;
import org.kraskovsky.polls.model.FieldProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldPropertyRepository extends JpaRepository<FieldProperty, Long> {
    void removeAllByField(Field f);
}