package org.kraskovsky.polls.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "field_properties")
@Data
public class FieldProperty extends BaseEntity {

    @NotBlank
    private String name;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "field_id")
    private Field field;

    public FieldProperty() {}

    public FieldProperty(String name, Field field) {
        this.name = name;
        this.field = field;
    }
}
