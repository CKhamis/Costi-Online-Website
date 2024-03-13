package com.costi.csw9.Model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@Table(name = "dynamic_content")
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
    private String wikiContent;

    @Column(columnDefinition="text")
    @NotBlank
    private String newsroomContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String minecraftHomeContent;

    @Column(columnDefinition="mediumtext")
    @NotNull
    @NotBlank
    private String minecraftGovernmentContent;

    @Column(columnDefinition="mediumtext")
    @NotNull
    @NotBlank
    private String aboutContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String axcelContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String professionalContent;

    @Column(columnDefinition="text")
    @NotNull
    @NotBlank
    private String treeContent;
}
