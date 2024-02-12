package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class DynamicContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @NotBlank
    private String label;

    @Column(nullable = false)
    private LocalDateTime lastEdited;

    @Column(nullable = false)
    @NotNull
    private boolean enabled = false;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String homeContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String projectContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String wikiContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String newsroomContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String minecraftHomeContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String minecraftGovernmentContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String aboutContent;
}
