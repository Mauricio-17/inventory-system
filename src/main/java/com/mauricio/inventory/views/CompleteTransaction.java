package com.mauricio.inventory.views;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "view_transaction")
@Subselect("select uuid() as id, tr.* from view_transaction tr")
@Getter
@Setter
public class CompleteTransaction implements Serializable {

    @Id
    private String id;
    private Long transactionId;
    private String description;
    private String employeeName;
    private String equipmentName;
    private Long locationProvenance;
    private Long locationDestination;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
