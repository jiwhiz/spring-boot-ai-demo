package com.jiwhiz.demo.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * An authority (a security role) used by Spring Security.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authority")
@Builder
public class Authority implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 50)
    private String name;
}
